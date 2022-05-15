package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.error.ValidationErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class ErrorService {
    private final ObjectMapper mapper;

    @Inject
    public ErrorService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Object readErrorMessage(HttpException httpException) {
        Response<?> response = httpException.response();
        if (response == null) {
            return null;
        }
        try (ResponseBody responseBody = response.errorBody()) {
            if (responseBody == null) {
                return null;
            }
            JsonNode node = mapper.readTree(responseBody.string());
            if (node.get("message") instanceof ArrayNode){
                return mapper.treeToValue(node, ValidationErrorResponse.class);
            }
            else {
                return mapper.treeToValue(node, ErrorResponse.class);
            }
        } catch (IOException e) {
            return null;
        }
    }
}
