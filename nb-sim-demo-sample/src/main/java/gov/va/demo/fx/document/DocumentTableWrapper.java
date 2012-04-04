/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.fx.document;

import gov.va.demo.nb.sim.jpa.Documents;
import java.text.SimpleDateFormat;

/**
 *
 * @author kec
 */
public class DocumentTableWrapper {
    
    private static SimpleDateFormat dobFormat = new SimpleDateFormat("dd MMM yyyy");
    private static SimpleDateFormat docTimeFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    
    private Documents doc;

    public DocumentTableWrapper(Documents doc) {
        this.doc = doc;
    }
    
    public String getPatientFirstName() {
        return doc.getPatientnid().getFirstname();
    }
    
    public String getPatientLastName() {
        return doc.getPatientnid().getLastname();
    }
    
    public String getPatientDOB() {
        return dobFormat.format(doc.getPatientnid().getDob());
    }
    
    public String getAuthorFirstName() {
        return doc.getProvidernid().getFirstname();
    }
    
    public String getAuthorLastName() {
        return doc.getProvidernid().getLastname();
    }
    
    public String getDocTime() {
        return docTimeFormat.format(doc.getInid().getIstart());
    }

    public int getDnid() {
        return doc.getDnid();
    }

    public Documents getDocument() {
        return doc;
    }
    
    
    
}
