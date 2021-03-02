
package com.weking.core.gash;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "settleSoap", targetNamespace = "http://egsys.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SettleSoap {


    /**
     * 
     * @param data
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "http://egsys.org/getResponse")
    @WebResult(name = "getResponseResult", targetNamespace = "http://egsys.org/")
    @RequestWrapper(localName = "getResponse", targetNamespace = "http://egsys.org/", className = "SettleWS.GetResponse")
    @ResponseWrapper(localName = "getResponseResponse", targetNamespace = "http://egsys.org/", className = "SettleWS.GetResponseResponse")
    public String getResponse(
            @WebParam(name = "data", targetNamespace = "http://egsys.org/")
                    String data);

}
