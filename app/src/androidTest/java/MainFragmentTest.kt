import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.github.repositories.R
import com.example.github.repositories.ui.MainActivity
import com.example.github.repositories.ui.adapters.GenericRecyclerAdapter
import com.example.github.repositories.ui.viewholder.RepoListItemViewHolder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainFragmentTest {


    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)


    @Test(expected = PerformException::class)
    fun check_if_title_are_updating_in_list_as_expected() {
        onView(ViewMatchers.withId(R.id.news_list))
            .perform(
                RecyclerViewActions.scrollTo<GenericRecyclerAdapter.ViewHolder>(
                    hasDescendant(withText("#5:google/iosched"))
                )
            )
    }

}