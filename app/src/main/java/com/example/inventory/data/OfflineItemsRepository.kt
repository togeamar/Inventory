import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import com.example.inventory.data.history
import com.example.inventory.data.itemDao
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: itemDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()

    override fun getallhistorystream(): Flow<List<history>> = itemDao.getAllhistory()

    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    override suspend fun deleteItem(item: Item) = itemDao.delete(item)
    override suspend fun deletehistory(history: history) {
        itemDao.deletehistory(history)
    }

    override suspend fun updateItem(item: Item) = itemDao.update(item)

    override suspend fun incselected(id: Int) {
        itemDao.incselected(id)
    }
    override suspend fun remselected(id: Int) {
        itemDao.remselected(id)
    }

    override suspend fun updatequantity() {
        itemDao.updatequantity()
    }

    override suspend fun updatecolor() {
        itemDao.updatecolor()
    }

    override suspend fun updatecolor2(id: Int) {
        itemDao.updatecolor2(id)
    }

    override suspend fun zeroselected() {
        itemDao.zeroselected()
    }

    override suspend fun inserthistory(history: history) {
        itemDao.inserthistory( history)
    }

}