// app/api/google-auth/route.ts
import { NextRequest, NextResponse } from "next/server";
import { setSessionCookie } from "@/lib/actions/auth.action";

const BACKEND_URL = "http://localhost:8080";

export async function POST(req: NextRequest) {
  try {
    const { uid, name, email, idToken } = await req.json();

    // Call Spring Boot signup to ensure user is registered
    await fetch(`${BACKEND_URL}/api/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ uid, name, email, password: "" })
    });

    // Call Spring Boot signin to verify and fetch credentials
    const signinRes = await fetch(`${BACKEND_URL}/api/auth/signin`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, idToken })
    });

    const signinData = await signinRes.json();
    if (signinData.success && signinData.sessionCookie) {
      await setSessionCookie(idToken);
      return NextResponse.json({ success: true, message: "User signed in successfully." });
    }

    return NextResponse.json({ success: false, message: "Failed to authenticate session with backend." }, { status: 401 });
  } catch (error) {
    console.error("Google Auth error:", error);
    return NextResponse.json({ success: false, message: "Something went wrong." }, { status: 500 });
  }
}

