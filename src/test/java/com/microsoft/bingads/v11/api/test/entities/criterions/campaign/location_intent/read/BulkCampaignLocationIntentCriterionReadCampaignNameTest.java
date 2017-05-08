package com.microsoft.bingads.v11.api.test.entities.criterions.campaign.location_intent.read;

import com.microsoft.bingads.internal.functionalinterfaces.Function;
import com.microsoft.bingads.v11.api.test.entities.criterions.campaign.location_intent.BulkCampaignLocationIntentCriterionTest;
import com.microsoft.bingads.v11.bulk.entities.BulkCampaignLocationIntentCriterion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class BulkCampaignLocationIntentCriterionReadCampaignNameTest extends BulkCampaignLocationIntentCriterionTest {

    @Parameter(value = 1)
    public String expectedResult;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][]{
                        {"123", "123"},
                        {"XXX YYY", "XXX YYY"},
                        {"", ""},
                        {null, null}
                }
        );
    }

    @Test
    public void testRead() {
        testReadProperty(
                "Campaign",
                datum,
                expectedResult,
                new Function<BulkCampaignLocationIntentCriterion, String>() {
                    @Override
                    public String apply(BulkCampaignLocationIntentCriterion c) {
                        return c.getCampaignName();
                    }
                }
        );
    }
}
