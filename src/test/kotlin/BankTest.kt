import confusedalex.thegoldeconomy.TheGoldEconomy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock

class BankTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: TheGoldEconomy

    @BeforeEach
    fun setUp() {
        // Start the mock server
        server = MockBukkit.mock()
        // Load your plugin
        plugin = MockBukkit.load(TheGoldEconomy::class.java)
    }

    @AfterEach
    fun tearDown() {
        // Stop the mock server
        MockBukkit.unmock()
    }

    @Test
    fun thisTestWillFail() {
        // Perform your test
    }
}