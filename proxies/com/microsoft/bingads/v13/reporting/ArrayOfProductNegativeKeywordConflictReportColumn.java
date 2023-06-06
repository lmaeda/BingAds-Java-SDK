
package com.microsoft.bingads.v13.reporting;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfProductNegativeKeywordConflictReportColumn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfProductNegativeKeywordConflictReportColumn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProductNegativeKeywordConflictReportColumn" type="{https://bingads.microsoft.com/Reporting/v13}ProductNegativeKeywordConflictReportColumn" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfProductNegativeKeywordConflictReportColumn", propOrder = {
    "productNegativeKeywordConflictReportColumns"
})
public class ArrayOfProductNegativeKeywordConflictReportColumn {

    @XmlElement(name = "ProductNegativeKeywordConflictReportColumn")
    @XmlSchemaType(name = "string")
    protected List<ProductNegativeKeywordConflictReportColumn> productNegativeKeywordConflictReportColumns;

    /**
     * Gets the value of the productNegativeKeywordConflictReportColumns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productNegativeKeywordConflictReportColumns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductNegativeKeywordConflictReportColumns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductNegativeKeywordConflictReportColumn }
     * 
     * 
     */
    public List<ProductNegativeKeywordConflictReportColumn> getProductNegativeKeywordConflictReportColumns() {
        if (productNegativeKeywordConflictReportColumns == null) {
            productNegativeKeywordConflictReportColumns = new ArrayList<ProductNegativeKeywordConflictReportColumn>();
        }
        return this.productNegativeKeywordConflictReportColumns;
    }

}
