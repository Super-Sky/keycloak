/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.operator.testsuite.unit;

import io.fabric8.kubernetes.client.utils.Serialization;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.keycloak.operator.crds.v2alpha1.deployment.Keycloak;
import org.keycloak.operator.crds.v2alpha1.deployment.spec.FeatureSpec;
import org.keycloak.operator.crds.v2alpha1.deployment.spec.TransactionsSpec;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CRSerializationTest {

    @Test
    public void testDeserialization() {
        Keycloak keycloak = Serialization.unmarshal(this.getClass().getResourceAsStream("/test-serialization-keycloak-cr.yml"), Keycloak.class);

        assertEquals("my-hostname", keycloak.getSpec().getHostname());
        assertEquals("my-image", keycloak.getSpec().getImage());
        assertEquals("my-tls-secret", keycloak.getSpec().getHttpSpec().getTlsSecret());
        assertTrue(keycloak.getSpec().isDisableDefaultIngress());

        final TransactionsSpec transactionsSpec = keycloak.getSpec().getTransactionsSpec();
        assertThat(transactionsSpec, notNullValue());
        assertThat(transactionsSpec.isXaEnabled(), notNullValue());
        assertThat(transactionsSpec.isXaEnabled(), CoreMatchers.is(false));
    }

    @Test
    public void featureSpecificationDeserialization(){
        Keycloak keycloak = Serialization.unmarshal(this.getClass().getResourceAsStream("/test-serialization-keycloak-cr.yml"), Keycloak.class);

        final FeatureSpec featureSpec = keycloak.getSpec().getFeatureSpec();
        assertThat(featureSpec, notNullValue());

        final List<String> enabledFeatures = featureSpec.getEnabledFeatures();
        assertThat(enabledFeatures.size(), CoreMatchers.is(2));
        assertThat(enabledFeatures.get(0), CoreMatchers.is("docker"));
        assertThat(enabledFeatures.get(1), CoreMatchers.is("authorization"));

        final List<String> disabledFeatures = featureSpec.getDisabledFeatures();
        assertThat(disabledFeatures.size(), CoreMatchers.is(2));
        assertThat(disabledFeatures.get(0), CoreMatchers.is("admin"));
        assertThat(disabledFeatures.get(1), CoreMatchers.is("step-up-authentication"));
    }

}