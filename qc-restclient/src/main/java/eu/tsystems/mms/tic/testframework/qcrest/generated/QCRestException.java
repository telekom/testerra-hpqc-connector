//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2013.04.16 um 09:20:24 AM CEST 
//

package eu.tsystems.mms.tic.testframework.qcrest.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java-Klasse für anonymous complex type.
 * 
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="ExceptionProperties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ExceptionProperty" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="StackTrace" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "id",
        "title",
        "exceptionProperties",
        "stackTrace"
})
@XmlRootElement(name = "QCRestException")
public class QCRestException {

    @XmlElement(name = "Id", required = true)
    protected Object id;
    @XmlElement(name = "Title", required = true)
    protected Object title;
    @XmlElement(name = "ExceptionProperties")
    protected QCRestException.ExceptionProperties exceptionProperties;
    @XmlElement(name = "StackTrace")
    protected Object stackTrace;

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return possible object is {@link Object }
     * 
     */
    public Object getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value allowed object is {@link Object }
     * 
     */
    public void setId(Object value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return possible object is {@link Object }
     * 
     */
    public Object getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value allowed object is {@link Object }
     * 
     */
    public void setTitle(Object value) {
        this.title = value;
    }

    /**
     * Ruft den Wert der exceptionProperties-Eigenschaft ab.
     * 
     * @return possible object is {@link QCRestException.ExceptionProperties }
     *
     */
    public QCRestException.ExceptionProperties getExceptionProperties() {
        return exceptionProperties;
    }

    /**
     * Legt den Wert der exceptionProperties-Eigenschaft fest.
     *
     * @param value allowed object is {@link QCRestException.ExceptionProperties }
     *
     */
    public void setExceptionProperties(QCRestException.ExceptionProperties value) {
        this.exceptionProperties = value;
    }

    /**
     * Ruft den Wert der stackTrace-Eigenschaft ab.
     *
     * @return possible object is {@link Object }
     *
     */
    public Object getStackTrace() {
        return stackTrace;
    }

    /**
     * Legt den Wert der stackTrace-Eigenschaft fest.
     *
     * @param value allowed object is {@link Object }
     *
     */
    public void setStackTrace(Object value) {
        this.stackTrace = value;
    }

    /**
     * <p>
     * Java-Klasse für anonymous complex type.
     *
     * <p>
     * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ExceptionProperty" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Value" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "exceptionProperty"
    })
    public static class ExceptionProperties {

        @XmlElement(name = "ExceptionProperty", required = true)
        protected List<ExceptionProperty> exceptionProperty;

        /**
         * Gets the value of the exceptionProperty property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
         * method for the exceptionProperty property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         *
         * <pre>
         * getExceptionProperty().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link QCRestException.ExceptionProperties.ExceptionProperty }
         *
         *@return exception property
         */
        public List<ExceptionProperty> getExceptionProperty() {
            if (exceptionProperty == null) {
                exceptionProperty = new ArrayList<ExceptionProperty>();
            }
            return this.exceptionProperty;
        }

        /**
         * <p>
         * Java-Klasse für anonymous complex type.
         * 
         * <p>
         * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Value" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ExceptionProperty {

            @XmlAttribute(name = "Name")
            protected String name;
            @XmlAttribute(name = "Value")
            protected String value;

            /**
             * Ruft den Wert der name-Eigenschaft ab.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getName() {
                return name;
            }

            /**
             * Legt den Wert der name-Eigenschaft fest.
             * 
             * @param value allowed object is {@link String }
             * 
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Ruft den Wert der value-Eigenschaft ab.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getValue() {
                return value;
            }

            /**
             * Legt den Wert der value-Eigenschaft fest.
             * 
             * @param value allowed object is {@link String }
             * 
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}
