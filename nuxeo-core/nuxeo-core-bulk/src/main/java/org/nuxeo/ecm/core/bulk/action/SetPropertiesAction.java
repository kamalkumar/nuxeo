/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Funsho David
 */

package org.nuxeo.ecm.core.bulk.action;

import static org.nuxeo.ecm.core.bulk.BulkServiceImpl.STATUS_STREAM;
import static org.nuxeo.lib.stream.computation.AbstractComputation.INPUT_1;
import static org.nuxeo.lib.stream.computation.AbstractComputation.OUTPUT_1;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PropertyException;
import org.nuxeo.ecm.core.bulk.action.computation.AbstractBulkComputation;
import org.nuxeo.lib.stream.computation.Topology;
import org.nuxeo.runtime.stream.StreamProcessorTopology;

/**
 * @since 10.2
 */
public class SetPropertiesAction implements StreamProcessorTopology {

    public static final String ACTION_NAME = "setProperties";

    @Override
    public Topology getTopology(Map<String, String> options) {
        return Topology.builder()
                       .addComputation(SetPropertyComputation::new,
                               Arrays.asList(INPUT_1 + ":" + ACTION_NAME, //
                                       OUTPUT_1 + ":" + STATUS_STREAM))
                       .build();
    }

    public static class SetPropertyComputation extends AbstractBulkComputation {

        private static final Logger log = LogManager.getLogger(SetPropertyComputation.class);

        public SetPropertyComputation() {
            super(ACTION_NAME);
        }

        @Override
        protected void compute(CoreSession session, List<String> ids, Map<String, Serializable> properties) {
            DocumentRef[] docRefs = ids.stream().map(IdRef::new).toArray(DocumentRef[]::new);
            DocumentModelList docs = session.getDocuments(docRefs);
            for (DocumentModel doc : docs) {
                for (Entry<String, Serializable> es : properties.entrySet()) {
                    try {
                        doc.setPropertyValue(es.getKey(), es.getValue());
                    } catch (PropertyException e) {
                        // TODO send to error stream
                        log.warn("Cannot write property: {} of document: {}", es.getKey(), doc.getId(), e);
                    }
                }
                try {
                    session.saveDocument(doc);
                } catch (PropertyException e) {
                    // TODO send to error stream
                    log.warn("Cannot save document: {}", doc.getId(), e);
                }
            }
        }
    }

}