/* 
 * Created on 20.02.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * A utility class for converting between JAXB annotated objects and XML.
 */
public final class MarshallingUtils {
    /**
     * Hide constructor.
     */
    private MarshallingUtils() {
    }

    /**
     * @param <T> the type we want to convert the XML into
     * @param clazz the class of the parameterized type
     * @param xml the instance XML description
     * @return a deserialization of the XML into an object of type T of class class <T>
     * @throws JAXBException Exception during deserialization.
     */
    @SuppressWarnings("unchecked")
    public static <T> T marshal(final Class<T> clazz, final String xml) throws JAXBException {
        T res;
        if (clazz == xml.getClass()) {
            res = (T) xml;
        }
        else {
            final JAXBContext ctx = JAXBContext.newInstance(clazz);
            final Unmarshaller marshaller = ctx.createUnmarshaller();
            res = (T) marshaller.unmarshal(new StringReader(xml));
        }
        return res;
    }

    /**
     * @param <T> the type to serialize
     * @param clazz the class of the type to serialize
     * @param object the instance containing the data to serialize
     * @return a string representation of the data.
     * @throws JAXBException Exception during serialization
     */
    public static <T> String unmarshal(final Class<T> clazz, final Object object) throws JAXBException {
        final JAXBContext ctx = JAXBContext.newInstance(clazz);
        final Marshaller marshaller = ctx.createMarshaller();
        final StringWriter entityXml = new StringWriter();
        marshaller.marshal(object, entityXml);
        return entityXml.toString();
    }
}
