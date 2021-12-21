/*
 * Testerra
 *
 * (C) 2013, Stefan Prasse, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
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
