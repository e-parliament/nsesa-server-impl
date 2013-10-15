package org.nsesa.server.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Date: 12/03/13 11:53
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Entity
@Table(name = "amendment")
public class AmendmentContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    /**
     * The public identifier for a logical amendment - this constant among all its revisions.
     */
    @Column(nullable = false, length = 64)
    private String amendmentContainerID;

    @ManyToOne(optional = false)
    private Document document;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar modificationDate;

    @ManyToOne(optional = false)
    private Person person;

    /**
     * Flag to keep track of the latest revision - significantly speeds up certain operations.
     */
    private boolean latestRevision;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private AmendmentContainer previousAmendmentContainer;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private AmendmentContainer parentAmendmentContainer;

    /**
     * A revision key that uniquely identifies this amendment revision.
     */
    @Column(nullable = false, length = 64, unique = true)
    private String revisionID;

    /**
     * The two letter ISO code of the (primary) language this amendment is created in.
     */
    @Column(nullable = false, length = 2)
    private String languageISO;

    /**
     * The type of action of this amendment (modification, deletion, creation, move, ...)
     */
    @Enumerated(EnumType.STRING)
    private AmendmentAction amendmentAction;

    /**
     * The status of an amendment. The initial status of an amendment is 'candidate'. Left as a String for
     * easier extension.
     */
    @Column(nullable = false, length = 32)
    private String amendmentContainerStatus = "candidate";

    /**
     * The serialized body/payload of this amendment. Can be XML or JSON, depending on what your backend provides.
     */
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * A reference to the source of this this amendment (meaning, the place where the amendment should be injected upon)
     */
    @OneToOne(cascade = {CascadeType.ALL}, optional = false)
    private AmendableWidgetReference sourceReference;

    /**
     * A list of one or more target references - which will be impacted by this amendment. For example, if an amendment
     * is made on a &lt;DEFINITION&gt; element, the target references would be every widget where the redefined
     * element is used.
     * <p/>
     * TODO the target references are not yet supported
     */
    @OneToMany(cascade = {CascadeType.ALL})
    private List<AmendableWidgetReference> targetReferences = new ArrayList<AmendableWidgetReference>();

    /**
     * A list of bundled amendment containers
     */
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JoinTable(name = "amendment_bundles")
    private List<String> bundledAmendmentContainers = new ArrayList<String>();

    public AmendmentContainer() {
    }

    public AmendmentAction getAmendmentAction() {
        return amendmentAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AmendmentContainer that = (AmendmentContainer) o;

        if (amendmentAction != that.amendmentAction) return false;
        if (!amendmentContainerStatus.equals(that.amendmentContainerStatus)) return false;
        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        if (!ID.equals(that.ID)) return false;
        if (languageISO != null ? !languageISO.equals(that.languageISO) : that.languageISO != null)
            return false;
        if (!revisionID.equals(that.revisionID)) return false;
        if (sourceReference != null ? !sourceReference.equals(that.sourceReference) : that.sourceReference != null)
            return false;
        if (targetReferences != null ? !targetReferences.equals(that.targetReferences) : that.targetReferences != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + (revisionID != null ? revisionID.hashCode() : 0);
        result = 31 * result + (languageISO != null ? languageISO.hashCode() : 0);
        result = 31 * result + (amendmentAction != null ? amendmentAction.hashCode() : 0);
        result = 31 * result + (amendmentContainerStatus != null ? amendmentContainerStatus.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (sourceReference != null ? sourceReference.hashCode() : 0);
        result = 31 * result + (targetReferences != null ? targetReferences.hashCode() : 0);
        return result;
    }

    public void setAmendmentAction(AmendmentAction amendmentAction) {
        this.amendmentAction = amendmentAction;
    }

    public String getAmendmentContainerStatus() {
        return amendmentContainerStatus;
    }

    public void setAmendmentContainerStatus(String amendmentContainerStatus) {
        this.amendmentContainerStatus = amendmentContainerStatus;
    }

    public AmendableWidgetReference getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(AmendableWidgetReference sourceReference) {
        this.sourceReference = sourceReference;
    }

    public List<AmendableWidgetReference> getTargetReferences() {
        return targetReferences;
    }

    public void setTargetReferences(ArrayList<AmendableWidgetReference> targetReferences) {
        this.targetReferences = targetReferences;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLanguageISO() {
        return languageISO;
    }

    public void setLanguageISO(String languageISO) {
        this.languageISO = languageISO;
    }

    public String getRevisionID() {
        return revisionID;
    }

    public void setRevisionID(String revisionID) {
        this.revisionID = revisionID;
    }

    public String getAmendmentContainerID() {
        return amendmentContainerID;
    }

    public void setAmendmentContainerID(String amendmentContainerID) {
        this.amendmentContainerID = amendmentContainerID;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long id) {
        this.ID = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person creator) {
        this.person = creator;
    }

    public boolean isLatestRevision() {
        return latestRevision;
    }

    public void setLatestRevision(boolean latestRevision) {
        this.latestRevision = latestRevision;
    }

    public AmendmentContainer getPreviousAmendmentContainer() {
        return previousAmendmentContainer;
    }

    public void setPreviousAmendmentContainer(AmendmentContainer previousAmendmentContainer) {
        this.previousAmendmentContainer = previousAmendmentContainer;
    }

    public void setTargetReferences(List<AmendableWidgetReference> targetReferences) {
        this.targetReferences = targetReferences;
    }

    public AmendmentContainer getParentAmendmentContainer() {
        return parentAmendmentContainer;
    }

    public void setParentAmendmentContainer(AmendmentContainer parentAmendmentContainer) {
        this.parentAmendmentContainer = parentAmendmentContainer;
    }

    public List<String> getBundledAmendmentContainers() {
        return bundledAmendmentContainers;
    }

    public void setBundledAmendmentContainers(List<String> bundledAmendmentContainers) {
        this.bundledAmendmentContainers = bundledAmendmentContainers;
    }
}
