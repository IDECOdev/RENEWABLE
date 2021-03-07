package com.example.renewable;

public class AnswersList2 {
   String  id="", downvalue = "", actualvalue = "", catchedvalue = "";


    public AnswersList2(String id, String downvalue, String actualvalue, String catchedvalue) {
        this.id = id;
        this.downvalue = downvalue;
        this.actualvalue = actualvalue;
        this.catchedvalue = catchedvalue;
    }

    public String getDownvalue() {
        return downvalue;
    }

    public String getActualvalue() {
        return actualvalue;
    }

    public String getCatchedvalue() {
        return catchedvalue;
    }

    public String getID() {
        return id;
    }

}
