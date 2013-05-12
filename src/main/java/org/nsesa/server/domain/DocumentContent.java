package org.nsesa.server.domain;

import javax.persistence.*;

/**
 * Date: 12/03/13 11:53
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Entity
@Table(name = "content")
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    /**
     * public key.
     */
    @Column(nullable = false, length = 64)
    private String documentID;

    /**
     * XML content.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    public DocumentContent() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
