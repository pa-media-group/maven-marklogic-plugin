package com.marklogic.maven;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class PlexusConfigurationUtils {
    public static class PlexusConfigurationAttributeMatchingPredicate implements Predicate<PlexusConfiguration> {

        private final String name;
        private final String value;

        private PlexusConfigurationAttributeMatchingPredicate(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static PlexusConfigurationAttributeMatchingPredicate attributeMatching(String attributeName, String attributeValue) {
            checkNotNull(attributeName, "Attribute name must not be null.");
            checkNotNull(attributeValue, "Attribute value must not be null.");

            return new PlexusConfigurationAttributeMatchingPredicate(attributeName, attributeValue);
        }

        @Override
        public boolean apply(@Nullable PlexusConfiguration input) {
            if (input == null) {
                return false;
            }
            try {
                return value.equals(input.getAttribute(name));
            } catch (PlexusConfigurationException e) {
                throw Throwables.propagate(e);
            }
        }

    }


}
