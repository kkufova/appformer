/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.mvp;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.Identifier;
import org.drools.guvnor.client.annotations.ResourceType;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

/**
 * Utilities for Identifiers
 */
@ApplicationScoped
public class IdentifierUtils {

    @Inject
    private IOCBeanManager iocManager;

    /**
     * Given a bean definition return it's @Identifier value
     * 
     * @param beanDefinition
     * @return List of possible identifier, empty if none
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<String> getIdentifier(final IOCBeanDef beanDefinition) {
        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        final List ids = new ArrayList(2);
        for ( Annotation a : annotations ) {
            if ( a instanceof Identifier ) {
                final Identifier identifier = (Identifier) a;
                ids.add( identifier.value() );
            } else if ( a instanceof ResourceType) {
                final ResourceType resourceType = (ResourceType) a;
                ids.add(resourceType.value());
            }
        }
        return ids;
    }

    /**
     * Get a set of Bean definitions that can handle the @Identifier
     * 
     * @param identifier
     * @return
     */
    @SuppressWarnings("rawtypes")
    //Don't return actual Activity instances as we'd need to release them later
    public Set<IOCBeanDef< ? >> getActivities(final String identifier) {
        if ( identifier == null ) {
            return Collections.emptySet();
        }
        final Collection<IOCBeanDef> allActivityBeans = iocManager.lookupBeans( Activity.class );
        final Set<IOCBeanDef< ? >> matchingActivityBeans = new HashSet<IOCBeanDef< ? >>();
        for ( IOCBeanDef activityBean : allActivityBeans ) {
            for (String id : getIdentifier( activityBean )) {
                if ( identifier.equalsIgnoreCase( id ) ) {
                    matchingActivityBeans.add( activityBean );
                }
            }
        }
        return Collections.unmodifiableSet( matchingActivityBeans );
    }

}
