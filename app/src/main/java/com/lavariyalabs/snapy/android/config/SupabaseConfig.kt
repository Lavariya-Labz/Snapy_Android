package com.lavariyalabs.snapy.android.config

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * SupabaseConfig - Singleton object that initializes Supabase
 */
object SupabaseConfig {

    // ========== YOUR CREDENTIALS ==========
    private const val SUPABASE_URL = "https://lriggiqgikqhsfqlojlj.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxyaWdnaXFnaWtxaHNmcWxvamxqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjUyNzkwMTQsImV4cCI6MjA4MDg1NTAxNH0.gAXy6WgSGUs4wty7CkH3hIaCAyXq_pW6xm0TmQFvYxs"

    /**
     * supabaseClient - The main Supabase client
     *
     * Used to make requests to your database
     * Initialized once when app starts
     */
    val supabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        // Configure which modules to use
        install(Postgrest)  // For database queries
        install(Auth)       // For user authentication (future)

        // Configure timeouts using the public API
        // This replaces the internal httpConfig usage
        requestTimeout = 60.seconds

        // Configure JSON serialization to be more lenient
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        })
    }
}
