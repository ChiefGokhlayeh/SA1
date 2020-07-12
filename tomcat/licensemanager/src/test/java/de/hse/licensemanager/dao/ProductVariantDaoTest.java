package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.UnitTestSupport;
import de.hse.licensemanager.model.ProductVariant;

public class ProductVariantDaoTest {
    @Before
    public void setupBeforeTest() {
        UnitTestSupport.initDatabase();
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
                .getProductVariant(UnitTestSupport.PRODUCT_VARIANT_ID_WINDOWS);
        assertThat(windows, notNullValue());
    }

    @Test
    public void testFoundProductVariantDataPopulated() {
        final ProductVariant windows = ProductVariantDao.getInstance()
                .getProductVariant(UnitTestSupport.PRODUCT_VARIANT_ID_WINDOWS);

        assertThat(windows.getProduct(), equalTo(UnitTestSupport.PRODUCT_VARIANT_PRODUCT_WINDOWS));
        assertThat(windows.getVersion(), equalTo(UnitTestSupport.PRODUCT_VARIANT_VERSION_WINDOWS));
    }

    @Test
    public void testQueryAllProductVariants() {
        final List<ProductVariant> productVariants = ProductVariantDao.getInstance().getProductVariants();

        assertThat(productVariants, not(empty()));
        assertThat(productVariants, hasSize(7));
        assertThat(productVariants.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder(UnitTestSupport.PRODUCT_VARIANT_ID_MATLAB,
                        UnitTestSupport.PRODUCT_VARIANT_ID_WINDOWS, UnitTestSupport.PRODUCT_VARIANT_ID_QUARTUS_OLD,
                        UnitTestSupport.PRODUCT_VARIANT_ID_QUARTUS_NEW, UnitTestSupport.PRODUCT_VARIANT_ID_PHOTOSHOP,
                        UnitTestSupport.PRODUCT_VARIANT_ID_CANOE_OLD, UnitTestSupport.PRODUCT_VARIANT_ID_CANOE_NEW));
    }
}
