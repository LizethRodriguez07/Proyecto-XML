package com.example.gestiondeventasonlinestore_dany

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.ajustarBarrasSistema() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(
            view.paddingLeft,
            barras.top + view.paddingTop,
            view.paddingRight,
            barras.bottom + view.paddingBottom
        )
        insets
    }
}