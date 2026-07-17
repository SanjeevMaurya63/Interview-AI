'use server';
import { cookies } from "next/headers";

const TWO_WEEKS = 60 * 60 * 24 * 14;
const BACKEND_URL = "http://localhost:8080";

export async function signUp(params: SignUpParams){
    try {
        const res = await fetch(`${BACKEND_URL}/api/auth/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params)
        });
        const data = await res.json();
        return data;
    } catch (e) {
        console.error('Error creating a user via Spring Boot:', e);
        return {
            success: false,
            message: 'Failed to create an account'
        };
    }
}

export async function signIn(params: SignInParams){
    try {
        const res = await fetch(`${BACKEND_URL}/api/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params)
        });
        
        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            return { success: false, message: errData.message || 'User does not exist. Create an account instead.' };
        }
        
        const data = await res.json();
        if (data.success && data.sessionCookie) {
            const cookieStore = await cookies();
            cookieStore.set('session', data.sessionCookie, {
                maxAge: TWO_WEEKS,
                httpOnly: true,
                secure: process.env.NODE_ENV === 'production',
                path: '/',
                sameSite: 'lax',
            });
            return { success: true };
        }
        return { success: false, message: data.message || 'Sign in failed' };
    } catch (e) {
        console.error("Sign in error via Spring Boot:", e);
        return { success: false, message: "Sign in failed" };
    }
}

export async function setSessionCookie(idToken: string){
    // This is now integrated inside the signin action or can be used as fallback
    try {
        const res = await fetch(`${BACKEND_URL}/api/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idToken, email: "" })
        });
        const data = await res.json();
        if (data.success && data.sessionCookie) {
            const cookieStore = await cookies();
            cookieStore.set('session', data.sessionCookie, {
                maxAge: TWO_WEEKS,
                httpOnly: true,
                secure: process.env.NODE_ENV === 'production',
                path: '/',
                sameSite: 'lax',
            });
        }
    } catch (e) {
        console.error("Error setting session cookie:", e);
    }
}

export async function getCurrentUser(): Promise<User | null> {
    const cookieStore = await cookies();
    const sessionCookie = cookieStore.get("session")?.value;

    if(!sessionCookie) return null;
    try {
        const res = await fetch(`${BACKEND_URL}/api/auth/me?sessionCookie=${encodeURIComponent(sessionCookie)}`);
        if (!res.ok) return null;
        const user = await res.json();
        return user;
    } catch (e) {
        console.log('Error verifying session cookie via Spring Boot:', e);
        return null;
    }
}

export async function isAuthenticated(){
    const user = await getCurrentUser();
    return !!user;
}

// Sign out user by clearing the session cookie
export async function signOut() {
    const cookieStore = await cookies();
    cookieStore.delete("session");
}



