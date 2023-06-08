package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Constants;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.CheckConnectionService;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;


public class AddressInputScreenController implements Controller{

    private final App app;
    private final ResourceBundle bundle;
    private final Provider<CheckConnectionService> checkConnectionService;
    private final Provider<LoginController> loginController;
    private final Provider<AddressInputScreenController> addressInputScreenController;
    public TextField addressTextField;
    public Button setAddressButton;
    private Disposable disposable;

    @Inject
    public AddressInputScreenController(App app, ResourceBundle bundle, Provider<CheckConnectionService> checkConnectionService,
                                        Provider<LoginController> loginController, Provider<AddressInputScreenController> addressInputScreenController) {
        this.app = app;
        this.bundle = bundle;
        this.checkConnectionService = checkConnectionService;
        this.loginController = loginController;
        this.addressInputScreenController = addressInputScreenController;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/addressInputScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        setAddressButton.setDefaultButton(true);

        return parent;
    }


    public void setAddress() {
        Constants.setURL(this.addressTextField.getText());
        disposable = this.checkConnectionService.get().checkConnection().observeOn(FX_SCHEDULER).subscribe(
                success -> {
                    this.app.show(this.loginController.get());
                },
                error -> {
                    this.app.showErrorDialog(bundle.getString("no.connection"), bundle.getString("no.connection.long"));
                    this.app.show(addressInputScreenController.get());
                }
        );
    }
}
