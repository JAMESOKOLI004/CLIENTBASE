package com.example

import com.example.clientbase.data.model.Customer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerModelTest {
    @Test
    fun customer_tagList_parsesCorrectly() {
        val customer = Customer(
            name = "Elena Vance",
            company = "Starlight Technologies",
            email = "elena.vance@starlighttech.io",
            phone = "+1 (415) 890-2134",
            address = "450 Mission St, San Francisco, CA",
            tags = "Enterprise, SaaS, High Value",
            status = "Active"
        )

        val tags = customer.getTagList()
        assertEquals(3, tags.size)
        assertTrue(tags.contains("Enterprise"))
        assertTrue(tags.contains("SaaS"))
        assertTrue(tags.contains("High Value"))
    }
}
