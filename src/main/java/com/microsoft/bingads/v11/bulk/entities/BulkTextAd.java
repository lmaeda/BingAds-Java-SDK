package com.microsoft.bingads.v11.bulk.entities;

import com.microsoft.bingads.v11.bulk.BulkServiceManager;
import com.microsoft.bingads.v11.campaignmanagement.AdType;
import com.microsoft.bingads.v11.campaignmanagement.ArrayOfKeyValuePairOfstringstring;
import com.microsoft.bingads.v11.campaignmanagement.KeyValuePairOfstringstring;
import com.microsoft.bingads.v11.campaignmanagement.TextAd;
import com.microsoft.bingads.v11.bulk.BulkFileReader;
import com.microsoft.bingads.v11.bulk.BulkFileWriter;
import com.microsoft.bingads.v11.bulk.BulkOperation;
import com.microsoft.bingads.v11.internal.bulk.StringTable;
import com.microsoft.bingads.v11.internal.bulk.BulkMapping;
import com.microsoft.bingads.v11.internal.bulk.MappingHelpers;
import com.microsoft.bingads.v11.internal.bulk.RowValues;
import com.microsoft.bingads.v11.internal.bulk.SimpleBulkMapping;
import com.microsoft.bingads.v11.internal.bulk.StringExtensions;
import com.microsoft.bingads.internal.functionalinterfaces.BiConsumer;
import com.microsoft.bingads.internal.functionalinterfaces.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a text ad.
 *
 * This class exposes the {@link #setTextAd} and {@link #getTextAd} methods
 * that can be used to read and write fields of the Text Ad record in a bulk file.
 *
 * <p>
 *     For more information, see Text Ad at
 *     <a href="https://go.microsoft.com/fwlink/?linkid=846127">https://go.microsoft.com/fwlink/?linkid=846127</a>
 * </p>
 *
 * @see BulkServiceManager
 * @see BulkOperation
 * @see BulkFileReader
 * @see BulkFileWriter
 */
public class BulkTextAd extends BulkAd<TextAd> {

    private static final List<BulkMapping<BulkTextAd>> MAPPINGS;

    static {
        List<BulkMapping<BulkTextAd>> m = new ArrayList<BulkMapping<BulkTextAd>>();

        m.add(new SimpleBulkMapping<BulkTextAd, String>(StringTable.Title,
                new Function<BulkTextAd, String>() {
                    @Override
                    public String apply(BulkTextAd c) {
                        return c.getAd().getTitle();
                    }
                },
                new BiConsumer<String, BulkTextAd>() {
                    @Override
                    public void accept(String v, BulkTextAd c) {
                        c.getAd().setTitle(v);
                    }
                }
        ));

        m.add(new SimpleBulkMapping<BulkTextAd, String>(StringTable.Text,
                new Function<BulkTextAd, String>() {
                    @Override
                    public String apply(BulkTextAd c) {
                        return c.getAd().getText();
                    }
                },
                new BiConsumer<String, BulkTextAd>() {
                    @Override
                    public void accept(String v, BulkTextAd c) {
                        c.getAd().setText(v);
                    }
                }
        ));

        m.add(new SimpleBulkMapping<BulkTextAd, String>(StringTable.DisplayUrl,
                new Function<BulkTextAd, String>() {
                    @Override
                    public String apply(BulkTextAd c) {
                        return c.getAd().getDisplayUrl();
                    }
                },
                new BiConsumer<String, BulkTextAd>() {
                    @Override
                    public void accept(String v, BulkTextAd c) {
                        c.getAd().setDisplayUrl(v);
                    }
                }
        ));

        m.add(new SimpleBulkMapping<BulkTextAd, String>(StringTable.DestinationUrl,
                new Function<BulkTextAd, String>() {
                    @Override
                    public String apply(BulkTextAd c) {
                        return StringExtensions.toOptionalBulkString(c.getAd().getDestinationUrl());
                    }
                },
                new BiConsumer<String, BulkTextAd>() {
                    @Override
                    public void accept(String v, BulkTextAd c) {
                        c.getAd().setDestinationUrl(StringExtensions.getValueOrEmptyString(v));
                    }
                },
                true
        ));
        
        m.add(new SimpleBulkMapping<BulkTextAd, String>(StringTable.NativePreference,
                new Function<BulkTextAd, String>() {
                    @Override
                    public String apply(BulkTextAd c) {
                        return StringExtensions.toNativePreferenceBulkString(c.getAd().getForwardCompatibilityMap());
                    }
                },
                new BiConsumer<String, BulkTextAd>() {
                    @Override
                    public void accept(String v, BulkTextAd c) {
                    	try{      
                    			if(c.getAd().getForwardCompatibilityMap() == null) {
                    				c.getAd().setForwardCompatibilityMap(new ArrayOfKeyValuePairOfstringstring());
                    			}          	    	                   	    	
                    	    	
                    	    	KeyValuePairOfstringstring keyValuePair = new KeyValuePairOfstringstring();
                    	    	
                    	    	keyValuePair.setKey("NativePreference");
                    	    	
                    	    	if (StringExtensions.isNullOrEmpty(v) || v.equals("All")) {
                    	    		keyValuePair.setValue("False");
                    	    	} 
                    	    	else if (v.equals("Native")) {
                    	    		keyValuePair.setValue("True");
                    	    	}
                    	    	else {
                    	    		throw new IllegalArgumentException(String.format("Unknown value for Native Preference: %s", v));
                    	    	}
                    	    	
                    	    	c.getAd().getForwardCompatibilityMap().getKeyValuePairOfstringstrings().add(keyValuePair);  
                    	    	
                    	} catch(Exception e) {
                    		e.printStackTrace();
                    	}                     
                    }
                },
                true
        ));
        
        MAPPINGS = Collections.unmodifiableList(m);
    }

    @Override
    public void processMappingsToRowValues(RowValues values, boolean excludeReadonlyData) {
        validatePropertyNotNull(getTextAd(), "TextAd");

        super.processMappingsToRowValues(values, excludeReadonlyData);

        MappingHelpers.<BulkTextAd>convertToValues(this, values, MAPPINGS);
    }

    @Override
    public void processMappingsFromRowValues(RowValues values) {
        this.setAd(new TextAd());
        
        this.getAd().setType(AdType.TEXT);
        
        super.processMappingsFromRowValues(values);
        MappingHelpers.<BulkTextAd>convertToEntity(values, MAPPINGS, this);
    }

    /**
     * Gets the text ad.
     */
    public TextAd getTextAd() {
        return this.ad;
    }

    /**
     * Sets the text ad.
     */
    public void setTextAd(TextAd ad) {
        this.setAd(ad);
    }
}
