package io.mindhouse.idee.ui.idea.list

import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeaListViewModel @Inject constructor(
        private val boardsRepository: BoardsRepository,
        @IOScheduler private val ioScheduler: Scheduler,
        private val exceptionHandler: ExceptionHandler
) : BaseViewModel<IdeaListViewState>() {

    override val initialState = IdeaListViewState(null, emptyList(), false)

    private var ideasDisposable: Disposable? = null

    var board: Board? = null
        set(value) {
            if (value != field) {
                field = value
                observeIdeas()
            }
        }

    //==========================================================================
    // private
    //==========================================================================

    private fun observeIdeas() {
        val board = board
        ideasDisposable?.dispose()

        if (board == null) {
            postState(initialState)
            return
        }

        postState(state.copy(board = board, isLoading = true))
        //// TODO: 29/06/2018 retry after delay
        val disposable = boardsRepository.observeIdeas(board.id)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onNext = ::onIdeas,
                        onError = ::onError
                )

        ideasDisposable = disposable
        addDisposable(disposable)
    }

    private fun onIdeas(ideas: List<Idea>) {
        postState(state.copy(isLoading = false, ideas = ideas))
    }

    private fun onError(throwable: Throwable) {
        Timber.e("Error observing ideas for board: $board")
        val msg = exceptionHandler.getErrorMessage(throwable)
        postState(state.copy(isLoading = false, errorMessage = msg))
        observeIdeas()
    }

}