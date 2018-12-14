package com.alfred.alfredtools;

import java.io.IOException;

public interface HttpResultListenerForSailfish extends HttpResultListener{

    public void onRespAuthorizeFailure(int source);

}
