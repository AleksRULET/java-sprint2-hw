package managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    @Override
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        super.init();
    }
}