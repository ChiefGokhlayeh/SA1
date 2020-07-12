package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.ProductVariant;

public class ProductVariantDaoTest {
    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(ProductVariantDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final IProductVariantDao a = ProductVariantDao.getInstance();
        final IProductVariantDao b = ProductVariantDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindProductVariantById() {
        final ProductVariant windows = ProductVariantDao.getInstance()
                .getProductVariant(PrepareTests.PRODUCT_VARIANT_ID_WINDOWS);
        assertThat(windows, notNullValue());
    }

    @Test
    public void testFoundProductVariantDataPopulated() {
        final ProductVariant windows = ProductVariantDao.getInstance()
                .getProductVariant(PrepareTests.PRODUCT_VARIANT_ID_WINDOWS);

        assertThat(windows.getProduct(), equalTo(PrepareTests.PRODUCT_VARIANT_PRODUCT_WINDOWS));
        assertThat(windows.getVersion(), equalTo(PrepareTests.PRODUCT_VARIANT_VERSION_WINDOWS));
    }

    @Test
    public void testQueryAllProductVariants() {
        final List<ProductVariant> productVariants = ProductVariantDao.getInstance().getProductVariants();

        assertThat(productVariants, not(empty()));
        assertThat(productVariants, hasSize(7));
        assertThat(productVariants.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder(PrepareTests.PRODUCT_VARIANT_ID_MATLAB, PrepareTests.PRODUCT_VARIANT_ID_WINDOWS,
                        PrepareTests.PRODUCT_VARIANT_ID_QUARTUS_OLD, PrepareTests.PRODUCT_VARIANT_ID_QUARTUS_NEW,
                        PrepareTests.PRODUCT_VARIANT_ID_PHOTOSHOP, PrepareTests.PRODUCT_VARIANT_ID_CANOE_OLD,
                        PrepareTests.PRODUCT_VARIANT_ID_CANOE_NEW));
    }
}
