"use client";

import { useRouter } from "next/navigation";

interface Game {
  id: string;
}

export default function HomePage() {
  const router = useRouter();

  // This is a comment
  const createNewGame = () => {
    fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/game/new`, {
      method: "POST",
    })
      .then((response) => response.json())
      .then((data: Game) => {
        router.push(`/${data.id}`);
      })
      .catch((error) => console.error("Error creating new game:", error));
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <h1 className="text-4xl font-bold mb-8">Codenames</h1>
      <button
        onClick={createNewGame}
        className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
      >
        Create New Game
      </button>
    </main>
  );
}
