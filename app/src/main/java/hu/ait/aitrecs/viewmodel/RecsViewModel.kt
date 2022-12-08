package hu.ait.aitrecs.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import hu.ait.aitrecs.AddRecActivity
import hu.ait.aitrecs.MapsActivity
import hu.ait.aitrecs.data.Rec

class RecsViewModel : ViewModel() {
    var recs: MutableLiveData<ArrayList<Rec>> = MutableLiveData<ArrayList<Rec>>()

    var snapshotListener: ListenerRegistration? = null

    fun queryRecs() {
        val queryRecs = FirebaseFirestore.getInstance().collection(
            AddRecActivity.COLLECTION_RECS
        )

        val eventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?,
                                 e: FirebaseFirestoreException?) {
                if (e != null) {
                    Log.w(TAG, "Listen Failed", e)
                    return
                }

                val allRecs = ArrayList<Rec>()

                for (docChange in querySnapshot?.documentChanges!!) {
                    val rec = docChange.document.toObject(Rec::class.java)
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        allRecs.add(rec)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {

                    } else if (docChange.type == DocumentChange.Type.MODIFIED) {

                    }
                }

                recs.value = allRecs
            }
        }

        snapshotListener = queryRecs.addSnapshotListener(eventListener)
    }

}