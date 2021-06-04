package tech.jforseth.cattledatabase.ui.cows;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Cows!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}