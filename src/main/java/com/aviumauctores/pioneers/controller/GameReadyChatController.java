package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.service.GroupService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.Parent;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class GameReadyChatController implements Controller{

    private final GroupService groupService;
    private final UserService userService;

    private final CompositeDisposable disposable = new CompositeDisposable();

    public GameReadyChatController(GroupService groupService, UserService userService){

        this.groupService = groupService;
        this.userService = userService;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
        disposable.dispose();
    }

    @Override
    public Parent render() {
        return null;
    }
}
