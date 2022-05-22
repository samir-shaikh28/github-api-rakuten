import com.example.github.repositories.ui.viewholder.RepoListItemViewHolder
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class MainFragmentUnitTest {

    var sampleDesc = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum"

    @Test
    fun check_if_desc_are_truncation_if_length_is_greater_than_150() {
       val result =  RepoListItemViewHolder.handleDescription(sampleDesc)
        assert(result.length < sampleDesc.length )
        // comparing with 153 as we have added ellipsize (...)
        assert(result.length <= 153)
    }
}