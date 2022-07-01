package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ErrorService {
    private final ObjectMapper mapper;

    private final App app;

    private final ResourceBundle bundle;

    public HashMap<String, String> errorCodes = new HashMap<>();

    @Inject
    public ErrorService(ObjectMapper mapper, App app, ResourceBundle bundle) {
        this.mapper = mapper;
        this.app = app;
        this.bundle = bundle;
    }

    public ErrorResponse readErrorMessage(HttpException httpException) {
        Response<?> response = httpException.response();
        if (response == null) {
            return null;
        }
        try (ResponseBody responseBody = response.errorBody()) {
            if (responseBody == null) {
                return null;
            }
            JsonNode node = mapper.readTree(responseBody.string());
            return mapper.treeToValue(node, ErrorResponse.class);
        } catch (IOException e) {
            return null;
        }
    }

    public void handleError(Throwable throwable) {
        if (throwable instanceof HttpException ex) {
            ErrorResponse response = this.readErrorMessage(ex);
            String message = errorCodes.get(Integer.toString(response.statusCode()));
            Platform.runLater(() -> app.showHttpErrorDialog(response.statusCode(), response.error(), message));
        } else {
            app.showErrorDialog(bundle.getString("connection.failed"), bundle.getString("try.again"));
        }
    }


    public void setErrorCodesLogin() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.username.password"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesLogout() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesUsers() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("other.user.error"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("409", bundle.getString("username.taken"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesGroups() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("change.group.error"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("409", bundle.getString("username.taken"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesMessages() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("inaccessible.parent"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesGameMembersPost() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("incorrect.password"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("409", bundle.getString("game.started.user.joined.error"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesGame() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("change.game.not.owner.error"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("409", bundle.getString("game.started.error"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesPioneersGet() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("not.member.of.game"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("409", bundle.getString("game.started.error"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesPioneersPost() {
        errorCodes.clear();
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("invalid.token"));
        errorCodes.put("403", bundle.getString("not.member.of.game.or.state"));
        errorCodes.put("404", bundle.getString("not.found"));
        errorCodes.put("409", bundle.getString("game.started.error"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesJoinGameController() {
        errorCodes.put("400", bundle.getString("validation.failed"));
        errorCodes.put("401", bundle.getString("incorrect.password"));
        errorCodes.put("404", bundle.getString("game.not.found"));
        errorCodes.put("409", bundle.getString("user.already.joined"));
        errorCodes.put("429", bundle.getString("limit.reached"));
    }

    public void setErrorCodesTradeController() {
        errorCodes.put("403" , bundle.getString("trade.error"));
    }

}
