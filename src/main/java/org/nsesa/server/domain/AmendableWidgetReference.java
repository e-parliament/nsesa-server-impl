/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package org.nsesa.server.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A reference to find the correct widget to amend, and used when passing information when creating a new element.
 * <p/>
 * Date: 10/07/12 22:34
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Entity
@Table(name = "reference")
public class AmendableWidgetReference implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * public primary key
     */
    @Column(name = "ref_id", length = 64, nullable = false)
    private String referenceID;
    /**
     * Boolean flag to see if the reference requires the creation of a new element before injecting
     */
    private boolean creation;

    /**
     * Boolean flag to see if the this reference is a sibling rather than a child, in case of a <tt>creation</tt>
     */
    private boolean sibling;
    /**
     * The namespace URI, if any.
     */
    @Column(name = "ref_ns", length = 255, nullable = false)
    private String namespaceURI;

    /**
     * The path to the matching node (usually XPath-like).
     */
    @Column(name = "ref_path", length = 255, nullable = false)
    private String path;

    /**
     * The type name of the widget to create.
     */
    @Column(name = "ref_type", length = 50, nullable = false)
    private String type;

    /**
     * The offset at which to create this widget under the parent overlay widget.
     */
    @Column(name = "ref_offset", length = 3)
    private int offset;

    public AmendableWidgetReference() {

    }

    public AmendableWidgetReference(String path) {
        this.path = path;
    }

    public AmendableWidgetReference(String path, String namespaceURI) {
        this.path = path;
        this.namespaceURI = namespaceURI;
    }

    public AmendableWidgetReference(boolean creation, boolean sibling, String path, String type, int offset) {
        this.creation = creation;
        this.sibling = sibling;
        this.path = path;
        this.type = type;
        this.offset = offset;
    }

    public AmendableWidgetReference(boolean creation, boolean sibling, String namespaceURI, String path, String type, int offset) {
        this.creation = creation;
        this.sibling = sibling;
        this.namespaceURI = namespaceURI;
        this.path = path;
        this.type = type;
        this.offset = offset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCreation() {
        return creation;
    }

    public void setCreation(boolean creation) {
        this.creation = creation;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isSibling() {
        return sibling;
    }

    public void setSibling(boolean sibling) {
        this.sibling = sibling;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }
}
