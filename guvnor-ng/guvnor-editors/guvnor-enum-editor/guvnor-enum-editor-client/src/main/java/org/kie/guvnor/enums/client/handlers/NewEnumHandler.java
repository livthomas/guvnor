package org.kie.guvnor.enums.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.enums.client.resources.i18n.Constants;
import org.kie.guvnor.enums.client.resources.images.ImageResources;
import org.kie.guvnor.enums.client.type.EnumResourceType;
import org.kie.guvnor.enums.service.EnumService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

/**
 * Handler for the creation of new Enumerations
 */
@ApplicationScoped
public class NewEnumHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<EnumService> enumService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private EnumResourceType resourceType;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newEnumDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.enumsIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        new SaveOperationService().save( contextPath,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 BusyPopup.showMessage( CommonConstants.INSTANCE.Saving() );
                                                 enumService.call( getSuccessCallback( presenter ),
                                                                   getErrorCallback() ).create( contextPath,
                                                                                                buildFileName( resourceType,
                                                                                                               baseFileName ),
                                                                                                "",
                                                                                                comment );
                                             }
                                         } );
    }

    private RemoteCallback<Path> getSuccessCallback( final NewResourcePresenter presenter ) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                BusyPopup.close();
                presenter.complete();
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path );
                placeManager.goTo( place );
            }
        };
    }

    private ErrorCallback getErrorCallback() {
        return new ErrorCallback() {

            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                //TODO Do something useful with the error!
                return true;
            }
        };
    }

}
