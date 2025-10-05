package ru.ssau.tk.swc.labs.exceptions;

public class InconsistentFunctionsException extends RuntimeException{
    public InconsistentFunctionsException (){
        super();
    }

    public InconsistentFunctionsException (String s){
        super(s);
    }
}
