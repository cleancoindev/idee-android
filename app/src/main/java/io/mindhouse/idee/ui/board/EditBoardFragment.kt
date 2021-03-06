package io.mindhouse.idee.ui.board

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.ui.base.MvvmFragment
import io.mindhouse.idee.ui.utils.SwipeOutRecyclerCallback
import io.mindhouse.idee.utils.SimpleTextWatcher
import io.mindhouse.idee.utils.isEmail
import kotlinx.android.synthetic.main.fragment_edit_board.*

/**
 * Created by kmisztal on 28/06/2018.
 *
 * @author Krzysztof Misztal
 */
class EditBoardFragment : MvvmFragment<EditBoardViewState, EditBoardViewModel>() {

    companion object {
        private const val KEY_BOARD = "board"

        //observe changes!!
        fun newInstance(board: Board? = null): EditBoardFragment {
            val fragment = EditBoardFragment()
            val args = Bundle()
            args.putParcelable(KEY_BOARD, board)

            fragment.arguments = args
            return fragment
        }
    }

    var fragmentCallbacks: FragmentCallbacks? = null

    private val board: Board? by lazy { arguments?.getParcelable(KEY_BOARD) as? Board }
    private val adapter = AttendeesRecyclerAdapter()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val board = board
        if (board != null) {
            viewModel.board = board
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_edit_board, container, false)

    @Suppress("UNUSED_EXPRESSION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun render(state: EditBoardViewState) {
        if (state.isLoading) {
            progressBar.visibility = View.VISIBLE
            saveButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            updateView()
        }

        if (state.isSaved) {
            fragmentCallbacks?.onBoardSaved()
        }

        if (state.attendees.isEmpty()) {
            notSharedText.visibility = View.VISIBLE
        } else {
            notSharedText.visibility = View.GONE

        }

        adapter.setItems(state.attendees)
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun addAttendee(emailAddress: String) {
        val context = context ?: return
        if (!emailAddress.isEmail) {
            Toast.makeText(context, R.string.wrong_email, Toast.LENGTH_SHORT).show()
            return
        }


        viewModel.addEmail(emailAddress)
        email.setText("")
    }

    private fun updateView() {
        val enabled = !boardNameText.text.isNullOrBlank()
        saveButton.isEnabled = enabled
    }

    private fun initViews(view: View) {
        boardNameText.setText(board?.name)

        //recycler view

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val swipeCallback = object : SwipeOutRecyclerCallback() {
            override fun getSwipeDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                val position = viewHolder?.adapterPosition
                if (position != null && adapter.data[position].role == R.string.role_owner) {
                    //we don't swipe owner!
                    return 0
                }

                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

        val helper = ItemTouchHelper(swipeCallback)
        helper.attachToRecyclerView(recyclerView)
        swipeCallback.onSwipedOut = { position ->
            //attendee removed
            val attendee = adapter.data[position]
            viewModel.removeEmail(attendee.email)
        }

        //texts

        updateView()
        boardNameText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateView()
            }
        })
        saveButton.setOnClickListener {
            val board = board
            if (board == null) {
                viewModel.createNewBoard(boardNameText.text.toString())
            } else {
                viewModel.updateBoard(boardNameText.text.toString())
            }
        }

        email.setOnEditorActionListener { editor, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val email = editor.text.toString()
                if (!email.isEmail) {
                    Toast.makeText(editor.context, R.string.wrong_email, Toast.LENGTH_SHORT).show()
                    false
                } else {
                    addAttendee(email)
                    true
                }
            }
            false
        }
    }

    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[EditBoardViewModel::class.java]

    interface FragmentCallbacks {
        fun onBoardSaved()
    }
}