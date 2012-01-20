package com.delect.motiver.server.manager;

import com.delect.motiver.shared.exception.AliasTakenException;
import com.delect.motiver.shared.exception.ConnectionException;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.delect.motiver.shared.exception.NotLoggedInException;
import com.prodeagle.java.counters.Counter;

public class AbstractManager {

  /**
   * Throws new exception based on given exception
   * @param source
   * @param e
   * @throws Exception
   */
  protected void handleException(String source, Exception e) throws ConnectionException {
    
    //ProdEagle
    if(e != null) {
      if(e instanceof AliasTakenException) {
        Counter.increment("Exception.AliasTaken");
        
        throw (AliasTakenException)e;
      }
      else if(e instanceof NoPermissionException) {
        Counter.increment("Exception.NoPermission");

        throw (NoPermissionException)e;
      }
      else if(e instanceof NotLoggedInException) {
        Counter.increment("Exception.NotLoggedIn");

        throw (NotLoggedInException)e;
      }
      else {
        Counter.increment("Exception.ConnectionException");

        throw new ConnectionException("source", e);
        
      }
    }
    Counter.increment("Exception");
    

    throw new ConnectionException("Unknown", "");
    
  }
}
