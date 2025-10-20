"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

interface Game {
  id: string;
}

export default function HomePage() {
  const router = useRouter();
  const [customWords, setCustomWords] = useState("");

  const createNewGame = () => {
    const words = customWords
      .split(',')
      .map(word => word.trim())
      .filter(word => word.length > 0);

    fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/game/new`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ customWords: words }),
    })
      .then((response) => response.json())
      .then((data: Game) => {
        router.push(`/${data.id}`);
      })
      .catch((error) => console.error("Error creating new game:", error));
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="flex flex-col items-center text-center w-full max-w-lg">
        <h1 className="text-4xl font-bold mb-8">Codenames</h1>
        <button
          onClick={createNewGame}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Create New Game
        </button>
        <textarea
          value={customWords}
          onChange={(e) => setCustomWords(e.target.value)}
          placeholder="Optional: Add your own words, separated by commas..."
          className="w-full p-2 border bg-gray-200 border-gray-300 rounded-md text-black mt-8"
          rows={6}
        />
      </div>
    </main>
  );
}