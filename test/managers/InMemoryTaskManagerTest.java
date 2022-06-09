package managers;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    @Override
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        super.init();
    }
}