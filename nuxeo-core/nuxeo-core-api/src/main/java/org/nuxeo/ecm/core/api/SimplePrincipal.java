/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     dragos
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api;

import java.io.Serializable;
import java.security.Principal;

/**
 * Used to change permission in connect.
 */
// FIXME remove this when connect will be changed to use SystemPrincipal
public class SimplePrincipal implements Principal, Serializable {

    private static final long serialVersionUID = 4899743263998931844L;

    final String id;

    public SimplePrincipal(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }

}