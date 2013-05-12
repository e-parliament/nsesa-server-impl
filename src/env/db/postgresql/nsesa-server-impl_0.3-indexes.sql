-- This file contains the minimal indexes required for decent DB performance.

-- Indexes for the 'amendment' table

CREATE INDEX amendment_amendmentID_idx ON amendment (amendmentcontainerid);
CREATE INDEX amendment_revisionID_idx ON amendment (revisionid);
CREATE INDEX amendment_documentID_idx ON amendment (document_id);
CREATE INDEX amendment_personID_idx ON amendment (person_id);

-- Indexes for the 'document' table
CREATE INDEX document_documentID_idx ON document (documentid);
CREATE INDEX document_languageiso_idx ON document (languageiso);

-- Indexes for the 'content' table
CREATE INDEX content_documentID_idx ON content (documentid);

-- Indexes for the 'person' table
CREATE INDEX person_personID_idx ON person (personid);

-- Indexes for the 'amendment_reference' table
CREATE INDEX amendment_reference_idx ON amendment_reference (amendment_id, targetreferences_id);