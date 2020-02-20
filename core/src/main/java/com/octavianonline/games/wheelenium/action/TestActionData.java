package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.ActionData;
import com.atsisa.gox.framework.serialization.annotation.Attribute;
import com.atsisa.gox.framework.serialization.annotation.Element;
import com.gwtent.reflection.client.annotations.Reflect_Full;

//@Reflect_Full
@Element
public class TestActionData extends ActionData {

    @Attribute
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
