package com.seamuslowry.branniganschess.backend.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NoAuthenticationIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    // jwt expired at epoch
    val expiredJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjowfQ.ASUuZVrHFUJbm2myuwYLezYm08MprJz16HTN5QYkwgt45gHo_OiBJUESa_SmJh9IfB63OAsnVt_CFhIHgZ-MtEUspWdyelNS1PNNXMMenbXCUlI39CK-omUYQ3klFaKF-DFnupdynLpYttoBDlGDZts50eWsPSURaaih1oj9d144lt6Si_-glDX0sPE_hrznGTaTtsZlAiJwnSM711ELRdlgGQkOBNCbuw-eVoW-RNixEJvy4B1dtvtZ9OZ6s-jfpFl-bDn0cCI7VKEallEEwBl-2tBT4VBsbPS7e6WLm1itSZIFwqvADdEpFxgUTCXery6vEBiWHDsp43Ou0IvrKg"

    @Test
    fun `Returns games when passing an expired JWT`() {

        mockMvc.get("/games") {
            header("Authorization", "Bearer $expiredJwt")
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `Returns healthy when passing an expired JWT`() {

        mockMvc.get("/health") {
            header("Authorization", "Bearer $expiredJwt")
        }.andExpect {
            status { isOk }
        }
    }
}