package org.nsesa.server.domain;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Date: 12/03/13 11:53
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    /**
     * public key.
     */
    @Column(nullable = false, length = 512)
    private String documentID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar modificationDate;

    /**
     * The name of the document.
     */
    @Column(nullable = false, length = 128)
    private String name;

    /**
     * The 2 letter ISO code for this translation.
     */
    @Column(nullable = false, length = 2)
    private String languageIso;

    /**
     * A flag indicating if this document is amendable or not.
     */
    private boolean amendable;

    /**
     * The deadline for this document.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deadline;

    public Document() {
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Calendar modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguageIso() {
        return languageIso;
    }

    public void setLanguageIso(String languageIso) {
        this.languageIso = languageIso;
    }

    public boolean isAmendable() {
        return amendable;
    }

    public void setAmendable(boolean amendable) {
        this.amendable = amendable;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }
}
