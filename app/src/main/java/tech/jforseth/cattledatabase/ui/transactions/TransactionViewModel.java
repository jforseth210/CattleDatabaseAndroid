package tech.jforseth.cattledatabase.ui.transactions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TransactionViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TransactionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Transactions");
    }

    public LiveData<String> getText() {
        return mText;
    }
}