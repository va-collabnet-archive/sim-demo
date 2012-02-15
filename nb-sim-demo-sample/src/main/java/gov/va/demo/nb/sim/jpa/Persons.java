/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.nb.sim.jpa;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kec
 */
@Entity
@Table(name = "PERSONS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Persons.findAll", query = "SELECT p FROM Persons p"),
    @NamedQuery(name = "Persons.findByPuuid", query = "SELECT p FROM Persons p WHERE p.puuid = :puuid"),
    @NamedQuery(name = "Persons.findByPnid", query = "SELECT p FROM Persons p WHERE p.pnid = :pnid"),
    @NamedQuery(name = "Persons.findByLastname", query = "SELECT p FROM Persons p WHERE p.lastname = :lastname"),
    @NamedQuery(name = "Persons.findByFirstname", query = "SELECT p FROM Persons p WHERE p.firstname = :firstname"),
    @NamedQuery(name = "Persons.findByDob", query = "SELECT p FROM Persons p WHERE p.dob = :dob")})
public class Persons implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "PUUID")
    private String puuid;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PNID")
    private Integer pnid;
    @Basic(optional = false)
    @Column(name = "LASTNAME")
    private String lastname;
    @Basic(optional = false)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Basic(optional = false)
    @Column(name = "DOB")
    @Temporal(TemporalType.DATE)
    private Date dob;
    @OneToMany(mappedBy = "providernid")
    private Collection<Documents> documentsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientnid")
    private Collection<Documents> documentsCollection1;

    public Persons() {
    }

    public Persons(Integer pnid) {
        this.pnid = pnid;
    }

    public Persons(Integer pnid, String puuid, String lastname, String firstname, Date dob) {
        this.pnid = pnid;
        this.puuid = puuid;
        this.lastname = lastname;
        this.firstname = firstname;
        this.dob = dob;
    }

    public String getPuuid() {
        return puuid;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    public Integer getPnid() {
        return pnid;
    }

    public void setPnid(Integer pnid) {
        this.pnid = pnid;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @XmlTransient
    public Collection<Documents> getDocumentsCollection() {
        return documentsCollection;
    }

    public void setDocumentsCollection(Collection<Documents> documentsCollection) {
        this.documentsCollection = documentsCollection;
    }

    @XmlTransient
    public Collection<Documents> getDocumentsCollection1() {
        return documentsCollection1;
    }

    public void setDocumentsCollection1(Collection<Documents> documentsCollection1) {
        this.documentsCollection1 = documentsCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pnid != null ? pnid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Persons)) {
            return false;
        }
        Persons other = (Persons) object;
        if ((this.pnid == null && other.pnid != null) || (this.pnid != null && !this.pnid.equals(other.pnid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.va.demo.nb.sim.jpa.Persons[ pnid=" + pnid + " ]";
    }
    
}
