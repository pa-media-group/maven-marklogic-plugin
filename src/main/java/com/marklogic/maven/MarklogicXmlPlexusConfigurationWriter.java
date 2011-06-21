package com.marklogic.maven;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * Write a plexus configuration to a stream
 * Note: This class was originally copied from plexus-container-default.  It is duplicated here
 * to maintain compatibility with both Maven 2.x and Maven 3.x.
 */
public class MarklogicXmlPlexusConfigurationWriter
{

    public void write( PlexusConfiguration configuration, Writer writer )
        throws IOException
    {
        int depth = 0;

        PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter( writer );
        write( configuration, xmlWriter, depth );
    }

    private void write( PlexusConfiguration c, XMLWriter w, int depth )
        throws IOException
    {
        int count = c.getChildCount();

        if ( count == 0 )
        {
            writeTag( c, w, depth );
        }
        else
        {
            w.startElement( c.getName() );
            writeAttributes( c, w );

            for ( int i = 0; i < count; i++ )
            {
                PlexusConfiguration child = c.getChild( i );

                write( child, w, depth + 1 );
            }

            w.endElement();
        }
    }

    private void writeTag( PlexusConfiguration c, XMLWriter w, int depth )
        throws IOException
    {
        w.startElement( c.getName() );

        writeAttributes( c, w );

        String value = c.getValue( null );
        if ( value != null )
        {
            w.writeText( value );
        }

        w.endElement();
    }

    private void writeAttributes( PlexusConfiguration c, XMLWriter w )
        throws IOException
    {
        String[] names = c.getAttributeNames();

        for ( int i = 0; i < names.length; i++ )
        {
            w.addAttribute( names[i], c.getAttribute( names[i], null ) );
        }

    }
}

