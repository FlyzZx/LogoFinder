package imt.logofinder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import imt.logofinder.analyzer.RemoteTraining;

/**
 * Created by 41000440 on 29/11/2017.
 */

public class RemoteTrainingTest {
    private RemoteTraining remoteTraining = null;

    @Before
    public void initialize() {
        remoteTraining = new RemoteTraining();
    }

    @Test
    public void checkBrands() {
        Assert.assertNotNull(remoteTraining.getBrands());
    }
}
