package ru.etu.graduatework.ui.attraction

import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.etu.graduatework.R
import ru.etu.graduatework.core.extension.dpToPx
import ru.etu.graduatework.core.extension.gone
import ru.etu.graduatework.core.extension.setVisibility
import ru.etu.graduatework.core.ui.BaseFragment
import ru.etu.graduatework.databinding.FragmentAttractionBinding
import ru.etu.graduatework.domain.model.Attraction
import ru.etu.graduatework.domain.model.Schedule
import ru.etu.graduatework.ui.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AttractionFragment : BaseFragment<FragmentAttractionBinding, AttractionViewModel>() {
    companion object {
        @JvmStatic
        val ARG_ID = "ARG_ID"

        @JvmStatic
        fun newInstance(id: Int): AttractionFragment {
            val args = Bundle().apply { putInt(ARG_ID, id) }
            val fragment = AttractionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAttractionBinding
        get() = { layoutInflater, viewGroup, attachToParent ->
            FragmentAttractionBinding.inflate(layoutInflater, viewGroup, attachToParent)
        }

    @Inject
    lateinit var viewModelAssistedFactory: AttractionViewModel.Factory

    override val viewModel: AttractionViewModel by viewModels {
        AttractionViewModel.provideFactory(viewModelAssistedFactory, getAttractionId())
    }
    private val activityViewModel: MainViewModel by activityViewModels()

    private var _behavior: BottomSheetBehavior<*>? = null
    private val behavior get() = _behavior!!

    private var callback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _behavior = BottomSheetBehavior.from(container!! as View)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStartVisibility()

        binding.btnAddRemove.setOnClickListener {
            activityViewModel.removePointIfExistOtherwiseAdd(getAttractionId())
        }
        binding.ibRefresh.setOnClickListener {
            viewModel.load()
            initStartVisibility()
        }

        binding.tbName.title = activityViewModel.attractionById.value?.get(getAttractionId())?.name
        observeAttraction()
        observeRoute()
        observeFailure()

        addBottomSheetCallBack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        behavior.removeBottomSheetCallback(callback!!)
        _behavior = null
        callback = null
    }

    private fun initStartVisibility() = setVisibilityForElements(true, false, false)

    private fun addBottomSheetCallBack() {
        callback = object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= 0)
                    binding.motion.progress = slideOffset
            }
        }
        behavior.addBottomSheetCallback(callback!!)
    }

    private fun observeAttraction() =
        viewModel.attraction.observe(viewLifecycleOwner) { attraction ->
            calculatePeekHeight(attraction)
            changeMotionMargin()

            binding.tbName.title = attraction.name

            if (attraction.type != null) binding.tvType.text = attraction.type
            else binding.tvType.gone()

            if (attraction.rating != null) binding.rb.rating = attraction.rating.toFloat()
            else binding.rb.gone()

            if (attraction.cost != null) binding.tvPrice.text = formatCost(attraction.cost)
            else binding.tvPrice.gone()

            binding.tvPrice.text = attraction.cost?.let { formatCost(it) }
            binding.tvDescription.text = attraction.description

            binding.tvBusinessHours.text =
                parseBusinessHours(attraction.schedule, attraction.isWorking)

            binding.tvBusinessHours.setOnClickListener {
                createSchedule(attraction.schedule)
            }

            attraction.pictureByteArray?.let {
                binding.ivPlace.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }

            resetBottomSheetState()
            setVisibilityForElements(false, false, true)
        }

    private fun parseBusinessHours(schedule: Schedule, isWorking: Boolean): String {
        if (schedule.list.isEmpty()) {
            return if (schedule.today.isWorking)
                "Открыто только сегодня до ${schedule.today.end}"
            else
                "Закрыто на неизвестный срок"
        }

        val open = { time: String -> "Открыто до ${time.dropLast(3)}" }
        val close = { time: String, day: String -> "Закрыто до ${time.dropLast(3)} $day" }

        var current =
            if (schedule.today.isWorking) schedule.list.find { it.day == schedule.today.day }
            else null

        val onlyToday =
            if (current == null ||
                !(current.start == schedule.today.start && current.end == schedule.today.end)
            ) {
                " (отлично от ежедневного)"
            } else ""

        val chooseByWorking = { endCurrent: String, startNext: String, dayNext: String ->
            if (isWorking) open(endCurrent) + onlyToday
            else close(startNext, dayNext)
        }

        val tmp = Schedule.Item(
            schedule.today.day,
            schedule.today.start ?: "",
            schedule.today.end ?: ""
        )
        current = current ?: tmp
        var next = schedule.list.find { it.day.value > current.day.value }
        if (next == null)
            next = schedule.list.find { it.day.value < current.day.value }

        return if (next != null) {
            if (current.day.value == (next.day.value - 1) % 7)
                chooseByWorking(current.end, next.start, "завтрашнего дня")
            else
                chooseByWorking(current.end, next.start, next.day.genitive)
        } else {
            chooseByWorking(current.end, current.start, current.day.genitive)
        }
    }

    private fun resetBottomSheetState() {
        when (behavior.state) {
            BottomSheetBehavior.STATE_HIDDEN -> behavior.state = BottomSheetBehavior.STATE_HIDDEN
            BottomSheetBehavior.STATE_COLLAPSED -> behavior.state =
                BottomSheetBehavior.STATE_COLLAPSED

            BottomSheetBehavior.STATE_EXPANDED -> behavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            BottomSheetBehavior.STATE_HALF_EXPANDED -> behavior.state =
                BottomSheetBehavior.STATE_HALF_EXPANDED

            BottomSheetBehavior.STATE_SETTLING -> behavior.state =
                BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun observeRoute() = activityViewModel.currentRoute.observe(viewLifecycleOwner) {
        if (it.pointIds.contains(getAttractionId()))
            changeButton("Удалить из маршрута", Color.parseColor("#B49594"))
        else
            changeButton("Добавить в маршрут", Color.parseColor("#659B5E"))
    }

    private fun observeFailure() = viewModel.failureEvent.observe(viewLifecycleOwner) {
        setVisibilityForElements(false, true, false)
        Toast.makeText(context, R.string.network_error_message, Toast.LENGTH_SHORT).show()
    }

    private fun setVisibilityForElements(
        progressbar: Boolean,
        refresh: Boolean,
        mainInfo: Boolean
    ) {
        binding.progressbar.setVisibility(progressbar)
        binding.nsvInfo.setVisibility(mainInfo)
        binding.flAddRemove.setVisibility(mainInfo)
        binding.ibRefresh.setVisibility(refresh)
    }

    private fun changeMotionMargin() {
        val constraints = binding.motion.getConstraintSet(R.id.start)
        constraints.setMargin(
            R.id.fl_add_remove,
            ConstraintSet.TOP,
            behavior.peekHeight - 80f.dpToPx()
        )
    }

    private fun formatCost(cost: Int): String {
        if (cost == 0)
            return "Бесплатно"
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = (Currency.getInstance("RUB"))
        return format.format(cost)
    }

    private fun calculatePeekHeight(attraction: Attraction) {
        var peekHeight = 136.dpToPx() + getRealTextViewHeight(16f) + 5f.dpToPx()
        val add = { obj: Any? ->
            if (obj != null) {
                peekHeight += getRealTextViewHeight(16f) + 5f.dpToPx()
            }
        }
        add(attraction.cost)
        add(attraction.type)
        if (attraction.rating != null)
            peekHeight += 16f.dpToPx() + 5f.dpToPx()

        behavior.peekHeight = peekHeight
    }

    private fun getRealTextViewHeight(textSize: Float): Int {
        val deviceWidth = requireActivity().windowManager.defaultDisplay.width
        val textView = TextView(context)
        textView.textSize = textSize
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.AT_MOST)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }

    fun getAttractionId() = requireArguments().getInt(ARG_ID)

    private fun changeButton(text: String, color: Int) {
        binding.btnAddRemove.text = text
        binding.btnAddRemove.setBackgroundColor(color)
        binding.btnAddRemove.invalidate()
    }

    private fun createSchedule(schedule: Schedule): AlertDialog {
        val descriptions = mutableMapOf<Schedule.DayOfWeek, String>()
        for (day in Schedule.DayOfWeek.values()) {
            descriptions[day] = "Закрыто"
        }
        for (item in schedule.list) {
            descriptions[item.day] =
                "Открыто с ${item.start.dropLast(3)} до ${item.end.dropLast(3)}"
        }
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
        return builder
            .setTitle(R.string.schedule)
            .setItems(
                descriptions.map { "${it.key.abbr}: ${it.value}" }.toTypedArray(),
                null
            ).setPositiveButton(R.string.OK) { _, _ ->
                childFragmentManager.popBackStack()
            }
            .show()
    }
}