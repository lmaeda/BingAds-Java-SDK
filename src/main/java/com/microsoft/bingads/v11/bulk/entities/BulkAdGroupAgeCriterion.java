package com.microsoft.bingads.v11.bulk.entities;

import com.microsoft.bingads.v11.bulk.BulkServiceManager;
import com.microsoft.bingads.v11.campaignmanagement.AdGroupCriterionStatus;
import com.microsoft.bingads.v11.campaignmanagement.AgeCriterion;
import com.microsoft.bingads.v11.campaignmanagement.AgeRange;
import com.microsoft.bingads.v11.campaignmanagement.BidMultiplier;
import com.microsoft.bingads.v11.campaignmanagement.BiddableAdGroupCriterion;
import com.microsoft.bingads.v11.campaignmanagement.CriterionBid;
import com.microsoft.bingads.internal.functionalinterfaces.BiConsumer;
import com.microsoft.bingads.internal.functionalinterfaces.Function;
import com.microsoft.bingads.v11.bulk.BulkFileReader;
import com.microsoft.bingads.v11.bulk.BulkFileWriter;
import com.microsoft.bingads.v11.bulk.BulkOperation;
import com.microsoft.bingads.v11.internal.bulk.BulkMapping;
import com.microsoft.bingads.v11.internal.bulk.MappingHelpers;
import com.microsoft.bingads.v11.internal.bulk.RowValues;
import com.microsoft.bingads.v11.internal.bulk.SimpleBulkMapping;
import com.microsoft.bingads.v11.internal.bulk.StringExtensions;
import com.microsoft.bingads.v11.internal.bulk.StringTable;
import com.microsoft.bingads.v11.internal.bulk.entities.SingleRecordBulkEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an age criterion that is assigned to an ad group. Each age criterion can be read or written in a bulk file.
 *
 * <p>
 *     For more information, see Ad Group Age Criterion at
 *     <a href="https://go.microsoft.com/fwlink/?linkid=846127>https://go.microsoft.com/fwlink/?linkid=846127</a>.
 * </p>
 *
 * @see BulkServiceManager
 * @see BulkOperation
 * @see BulkFileReader
 * @see BulkFileWriter
 */
public class BulkAdGroupAgeCriterion extends SingleRecordBulkEntity {
	
	private BiddableAdGroupCriterion adGroupCriterion;
	
	private String campaignName;
	
	private String adGroupName;

	private static final List<BulkMapping<BulkAdGroupAgeCriterion>> MAPPINGS;

    static {
        List<BulkMapping<BulkAdGroupAgeCriterion>> m = new ArrayList<BulkMapping<BulkAdGroupAgeCriterion>>();
        
        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, String>(StringTable.Status,
                new Function<BulkAdGroupAgeCriterion, String>() {
                    @Override
                    public String apply(BulkAdGroupAgeCriterion c) {
                        AdGroupCriterionStatus status = c.getAdGroupCriterion().getStatus();

                        return status == null ? null : status.value();
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                        c.getAdGroupCriterion().setStatus(StringExtensions.parseOptional(v, new Function<String, AdGroupCriterionStatus>() {
                            @Override
                            public AdGroupCriterionStatus apply(String s) {
                                return AdGroupCriterionStatus.fromValue(s);
                            }
                        }));
                    }
                }
        ));

        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, Long>(StringTable.Id,
                new Function<BulkAdGroupAgeCriterion, Long>() {
                    @Override
                    public Long apply(BulkAdGroupAgeCriterion c) {
                        return c.getAdGroupCriterion().getId();
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                        c.getAdGroupCriterion().setId(StringExtensions.parseOptional(v, new Function<String, Long>() {
                            @Override
                            public Long apply(String s) {
                                return Long.parseLong(s);
                            }
                        }));
                    }
                }
        ));

        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, Long>(StringTable.ParentId,
                new Function<BulkAdGroupAgeCriterion, Long>() {
                    @Override
                    public Long apply(BulkAdGroupAgeCriterion c) {
                        return c.getAdGroupCriterion().getAdGroupId();
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                        c.getAdGroupCriterion().setAdGroupId(Long.parseLong(v));
                    }
                }
        ));

        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, String>(StringTable.Campaign,
                new Function<BulkAdGroupAgeCriterion, String>() {
                    @Override
                    public String apply(BulkAdGroupAgeCriterion c) {
                        return c.getCampaignName();
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                        c.setCampaignName(v);
                    }
                }
        ));
        
        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, String>(StringTable.AdGroup,
                new Function<BulkAdGroupAgeCriterion, String>() {
                    @Override
                    public String apply(BulkAdGroupAgeCriterion c) {
                        return c.getAdGroupName();
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                        c.setAdGroupName(v);
                    }
                }
        ));
        
        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, String>(StringTable.BidAdjustment,
                new Function<BulkAdGroupAgeCriterion, String>() {
                    @Override
                    public String apply(BulkAdGroupAgeCriterion c) {
                        if (c.getAdGroupCriterion() instanceof BiddableAdGroupCriterion) {
                            CriterionBid bid = ((BiddableAdGroupCriterion) c.getAdGroupCriterion()).getCriterionBid();
                            if (bid == null) {
                                return null;
                            } else {
                                return StringExtensions.toCriterionBidMultiplierBulkString(((BidMultiplier) bid).getMultiplier());
                            }
                        } else {
                            return null;
                        }
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                        if (c.getAdGroupCriterion() instanceof BiddableAdGroupCriterion) {
                            ((BidMultiplier) ((BiddableAdGroupCriterion) c.getAdGroupCriterion()).getCriterionBid()).setMultiplier(
                                    StringExtensions.nullOrDouble(v)
                            );
                        }
                    }
                }
        ));
        
        m.add(new SimpleBulkMapping<BulkAdGroupAgeCriterion, String>(StringTable.Target,
                new Function<BulkAdGroupAgeCriterion, String>() {
                    @Override
                    public String apply(BulkAdGroupAgeCriterion c) { 
                    	if (c.getAdGroupCriterion().getCriterion() instanceof AgeCriterion) {    
                    		AgeRange ageRange = ((AgeCriterion) c.getAdGroupCriterion().getCriterion()).getAgeRange();
                    		return ageRange == null ? null : ageRange.value();
                    	} 
                    	return null;
                    }
                },
                new BiConsumer<String, BulkAdGroupAgeCriterion>() {
                    @Override
                    public void accept(String v, BulkAdGroupAgeCriterion c) {
                    	if (c.getAdGroupCriterion().getCriterion() instanceof AgeCriterion) {
                    		
	                		((AgeCriterion)c.getAdGroupCriterion().getCriterion()).setAgeRange(StringExtensions.parseOptional(v, new Function<String, AgeRange>() {
	                                @Override
	                                public AgeRange apply(String s) {
	                                    return AgeRange.fromValue(s);
	                                }
	                            }));
                    	}
                    }
                }
        ));

        MAPPINGS = Collections.unmodifiableList(m);
    }

    @Override
    public void processMappingsFromRowValues(RowValues values) {
        BiddableAdGroupCriterion adGroupCriterion = new BiddableAdGroupCriterion();  
        
        BidMultiplier bidMultiplier = new BidMultiplier();       
        bidMultiplier.setType(BidMultiplier.class.getSimpleName());
        
        AgeCriterion ageCriterion = new AgeCriterion();
        
    	adGroupCriterion.setCriterion(ageCriterion);
    	adGroupCriterion.getCriterion().setType(AgeCriterion.class.getSimpleName());
    	adGroupCriterion.setCriterionBid(bidMultiplier);
    	adGroupCriterion.setType("BiddableAdGroupCriterion");
    	
    	setAdGroupCriterion(adGroupCriterion);  
    	
    	MappingHelpers.convertToEntity(values, MAPPINGS, this);   
    }

    @Override
    public void processMappingsToRowValues(RowValues values, boolean excludeReadonlyData) {
        validatePropertyNotNull(getAdGroupCriterion(), BiddableAdGroupCriterion.class.getSimpleName());
        
        MappingHelpers.convertToValues(this, values, MAPPINGS);
    }

    /**
     * Gets an Ad Group Criterion.
     */
    public BiddableAdGroupCriterion getAdGroupCriterion() {
        return adGroupCriterion;
    }

    /**
     * Sets an Ad Group Criterion
     */
    public void setAdGroupCriterion(BiddableAdGroupCriterion adGroupCriterion) {
        this.adGroupCriterion = adGroupCriterion;
    }

    /**
     * Gets the name of the campaign.
     * Corresponds to the 'Campaign' field in the bulk file.
     */
    public String getCampaignName() {
        return campaignName;
    }

    /**
     * Sets the name of the ad group.
     * Corresponds to the 'Ad Group' field in the bulk file.
     */
    public void setAdGroupName(String adGroupName) {
        this.adGroupName = adGroupName;
    }
    
    /**
     * Gets the name of the ad group.
     * Corresponds to the 'Ad Group' field in the bulk file.
     */
    public String getAdGroupName() {
        return adGroupName;
    }

    /**
     * Sets the name of the campaign.
     * Corresponds to the 'Campaign' field in the bulk file.
     */
    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
}
