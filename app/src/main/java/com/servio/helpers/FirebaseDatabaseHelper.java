package com.servio.helpers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.servio.interfaces.SimpleCallback;
import com.servio.models.Dish;
import com.servio.models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseDatabaseHelper {
    private final FirebaseFirestore firebaseFirestoreReference;
    private final Context context;

    public FirebaseDatabaseHelper(FirebaseFirestore firebaseFirestoreReference, Context context) {
        this.firebaseFirestoreReference = firebaseFirestoreReference;
        this.context = context;
    }

    public void insertData(String collectionPath, String documentPath, Map<String, Object> cell) {
        firebaseFirestoreReference.collection(collectionPath).document(documentPath).set(cell).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(HallActivity.this, "Datele au fost adaugate cu succes", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Eroare la adaugarea datelor", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }

    public void deleteDocument(String collectionPath, String documentPath) {
        firebaseFirestoreReference.collection(collectionPath).document(documentPath).delete().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(HallActivity.this, "Datele au fost sterse cu succes", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Eroare la stergerea datelor", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void deleteCollection(String collectionPath) {
        firebaseFirestoreReference.collection(collectionPath).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            documentSnapshot.getReference().delete();
                        }
                    }
                }
        );
    }

    public <E> void simpleUpdateField(String collectionPath, String documentPath, final String fieldToUpdate, final E fieldToUpdateValue) {
        firebaseFirestoreReference.collection(collectionPath).document(documentPath).update(fieldToUpdate, fieldToUpdateValue).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(context, "Datele au fost actualizate cu succes", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Eroare la actualizarea datelor", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public <E> void updateFields(String collectionPath, final String fieldToFilter, final String fieldToFilterValue, final String fieldToUpdate, final E fieldToUpdateValue) {
        final CollectionReference collectionReference = firebaseFirestoreReference.collection(collectionPath);
        collectionReference.whereEqualTo(fieldToFilter, fieldToFilterValue).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                Map<String, E> map = new HashMap<>();
                                map.put(fieldToUpdate, fieldToUpdateValue);
                                collectionReference.document(documentSnapshot.getId()).set(map, SetOptions.merge());
                            }
                        }
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Eroare la actualizarea datelor", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void checkEmptyCollection(String collectionPath, final SimpleCallback<Boolean> finishedCallback) {
        CollectionReference collectionReference = firebaseFirestoreReference.collection(collectionPath);
        collectionReference.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null) {
                                finishedCallback.callback(document.isEmpty());
                            }
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void getDishesData(final SimpleCallback<List<Dish>> finishedCallback) {
        firebaseFirestoreReference.collection("Dishes").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Dish> list = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Dish dish = document.toObject(Dish.class);
                                list.add(dish);
                            }
                            finishedCallback.callback(list);
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void getOrdersData(String fieldToFilter, String fieldToFilterValue, final SimpleCallback<List<Order>> finishedCallback) {
        firebaseFirestoreReference.collection("Orders").whereEqualTo(fieldToFilter, fieldToFilterValue).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Order> list = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Order order = document.toObject(Order.class);
                                list.add(order);
                            }
                            finishedCallback.callback(list);
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public <E> void getDocument(String collectionPath, String documentPath, final Class<E> className, final SimpleCallback<E> finishedCallback) {
        firebaseFirestoreReference.collection(collectionPath).document(documentPath).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            finishedCallback.callback(document.toObject(className));
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void getDataFieldsValues(String collectionPath, final String field, final SimpleCallback<List<String>> finishedCallback) {
        firebaseFirestoreReference.collection(collectionPath).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> fieldsValues = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String value = document.getString(field);
                                fieldsValues.add(value);
                            }
                            finishedCallback.callback(fieldsValues);
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public <E> void getSingleDataFieldValue(String collectionPath, final String documentPath, final String field, final SimpleCallback<E> finishedCallback) {
        firebaseFirestoreReference.collection(collectionPath).document(documentPath).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            E fieldValue = (E) Objects.requireNonNull(document).get(field);
                            finishedCallback.callback(fieldValue);
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void getSingleDataFieldValueWithCondition(String collectionPath, final String field, final String conditionField, final String conditionFieldValue, final SimpleCallback<String> finishedCallback) {
        final CollectionReference collectionReference = firebaseFirestoreReference.collection(collectionPath);
        collectionReference.whereEqualTo(conditionField, conditionFieldValue).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String fieldValue = "";
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                fieldValue = (String) documentSnapshot.get(field);
                            }
                            finishedCallback.callback(fieldValue);

                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void getSingleLongFieldValueWithCondition(String collectionPath, final String field, final String conditionField, final String conditionFieldValue, final SimpleCallback<Long> finishedCallback) {
        final CollectionReference collectionReference = firebaseFirestoreReference.collection(collectionPath);
        collectionReference.whereEqualTo(conditionField, conditionFieldValue).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Long fieldValue = null;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                fieldValue = (Long) documentSnapshot.get(field);
                            }
                            finishedCallback.callback(fieldValue);

                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void checkIfDocumentExists(String collectionPath, String documentPath, final SimpleCallback<Boolean> finishedCallback) {
        final DocumentReference documentReference = firebaseFirestoreReference.collection(collectionPath).document(documentPath);
        documentReference.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            finishedCallback.callback(document.exists());
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void deleteDocumentsWithGreaterThanCondition(String collectionPath, final String field, final String value) {
        final CollectionReference collectionReference = firebaseFirestoreReference.collection(collectionPath);
        collectionReference.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            String fieldValue = (String) documentSnapshot.get(field);

                            if (fieldValue.startsWith(value)) {
                                documentSnapshot.getReference().delete();
                            }
                        }
                    }
                }
        );
    }

    public void getCollectionDocumentsCount(String collectionPath, final SimpleCallback<Integer> finishedCallback) {
        final CollectionReference collectionReference = firebaseFirestoreReference.collection(collectionPath);
        collectionReference.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot ignored : task.getResult()) {
                                count++;
                            }
                            finishedCallback.callback(count);
                        } else {
                            Toast.makeText(context, "Eroare", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
