<template>
  <div class="editor-container">
    <div v-if="editor" class="editor-menu">
      <button
        @click="editor.chain().focus().toggleBold().run()"
        :class="{ 'is-active': editor.isActive('bold') }"
        class="menu-btn"
        title="Bold"
      >
        <strong>B</strong>
      </button>
      <button
        @click="editor.chain().focus().toggleItalic().run()"
        :class="{ 'is-active': editor.isActive('italic') }"
        class="menu-btn"
        title="Italic"
      >
        <em>I</em>
      </button>
      <button
        @click="editor.chain().focus().toggleStrike().run()"
        :class="{ 'is-active': editor.isActive('strike') }"
        class="menu-btn"
        title="Strikethrough"
      >
        <s>S</s>
      </button>
      <button
        @click="editor.chain().focus().toggleCode().run()"
        :class="{ 'is-active': editor.isActive('code') }"
        class="menu-btn"
        title="Code"
      >
        Code
      </button>

      <span class="separator"></span>

      <button
        @click="editor.chain().focus().toggleHeading({ level: 1 }).run()"
        :class="{ 'is-active': editor.isActive('heading', { level: 1 }) }"
        class="menu-btn"
        title="Heading 1"
      >
        H1
      </button>
      <button
        @click="editor.chain().focus().toggleHeading({ level: 2 }).run()"
        :class="{ 'is-active': editor.isActive('heading', { level: 2 }) }"
        class="menu-btn"
        title="Heading 2"
      >
        H2
      </button>
      <button
        @click="editor.chain().focus().toggleHeading({ level: 3 }).run()"
        :class="{ 'is-active': editor.isActive('heading', { level: 3 }) }"
        class="menu-btn"
        title="Heading 3"
      >
        H3
      </button>

      <span class="separator"></span>

      <button
        @click="editor.chain().focus().toggleBulletList().run()"
        :class="{ 'is-active': editor.isActive('bulletList') }"
        class="menu-btn"
        title="Bullet List"
      >
        • List
      </button>
      <button
        @click="editor.chain().focus().toggleOrderedList().run()"
        :class="{ 'is-active': editor.isActive('orderedList') }"
        class="menu-btn"
        title="Ordered List"
      >
        1. List
      </button>
      <button
        @click="editor.chain().focus().toggleCodeBlock().run()"
        :class="{ 'is-active': editor.isActive('codeBlock') }"
        class="menu-btn"
        title="Code Block"
      >
        { }
      </button>
      <button
        @click="editor.chain().focus().toggleBlockquote().run()"
        :class="{ 'is-active': editor.isActive('blockquote') }"
        class="menu-btn"
        title="Quote"
      >
        "
      </button>

      <span class="separator"></span>

      <button
        @click="setLink"
        :class="{ 'is-active': editor.isActive('link') }"
        class="menu-btn"
        title="Add Link"
      >
        Link
      </button>
      <button
        @click="addImage"
        class="menu-btn"
        title="Add Image"
      >
        Image
      </button>

      <span class="separator"></span>

      <button
        @click="editor.chain().focus().insertTable({ rows: 3, cols: 3, withHeaderRow: true }).run()"
        class="menu-btn"
        title="Insert Table"
      >
        Table
      </button>

      <span class="separator"></span>

      <button
        @click="editor.chain().focus().undo().run()"
        :disabled="!editor.can().undo()"
        class="menu-btn"
        title="Undo"
      >
        ↶
      </button>
      <button
        @click="editor.chain().focus().redo().run()"
        :disabled="!editor.can().redo()"
        class="menu-btn"
        title="Redo"
      >
        ↷
      </button>
    </div>

    <editor-content :editor="editor" class="editor-content" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Link from '@tiptap/extension-link'
import Image from '@tiptap/extension-image'
import Table from '@tiptap/extension-table'
import TableRow from '@tiptap/extension-table-row'
import TableCell from '@tiptap/extension-table-cell'
import TableHeader from '@tiptap/extension-table-header'
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight'
import { common, createLowlight } from 'lowlight'

const lowlight = createLowlight(common)

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const editor = useEditor({
  extensions: [
    StarterKit.configure({
      codeBlock: false
    }),
    Link.configure({
      openOnClick: false
    }),
    Image,
    Table.configure({
      resizable: true
    }),
    TableRow,
    TableHeader,
    TableCell,
    CodeBlockLowlight.configure({
      lowlight
    })
  ],
  content: props.modelValue,
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML())
  }
})

watch(() => props.modelValue, (value) => {
  const isSame = editor.value.getHTML() === value
  if (isSame) {
    return
  }
  editor.value.commands.setContent(value, false)
})

const setLink = () => {
  const previousUrl = editor.value.getAttributes('link').href
  const url = window.prompt('URL', previousUrl)

  if (url === null) {
    return
  }

  if (url === '') {
    editor.value.chain().focus().extendMarkRange('link').unsetLink().run()
    return
  }

  editor.value.chain().focus().extendMarkRange('link').setLink({ href: url }).run()
}

const addImage = () => {
  const url = window.prompt('Image URL')

  if (url) {
    editor.value.chain().focus().setImage({ src: url }).run()
  }
}

onBeforeUnmount(() => {
  editor.value?.destroy()
})
</script>

<style scoped>
.editor-container {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.editor-menu {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
  padding: 0.5rem;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.menu-btn {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.menu-btn:hover:not(:disabled) {
  background: #e9ecef;
}

.menu-btn.is-active {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

.menu-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.separator {
  width: 1px;
  background: #ddd;
  margin: 0 0.5rem;
}

.editor-content {
  min-height: 400px;
  max-height: 600px;
  overflow-y: auto;
}

:deep(.ProseMirror) {
  padding: 1rem;
  min-height: 400px;
  outline: none;
}

:deep(.ProseMirror p) {
  margin: 0.5rem 0;
}

:deep(.ProseMirror h1) {
  font-size: 2rem;
  font-weight: bold;
  margin: 1rem 0;
}

:deep(.ProseMirror h2) {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0.75rem 0;
}

:deep(.ProseMirror h3) {
  font-size: 1.25rem;
  font-weight: bold;
  margin: 0.5rem 0;
}

:deep(.ProseMirror ul),
:deep(.ProseMirror ol) {
  padding-left: 2rem;
  margin: 0.5rem 0;
}

:deep(.ProseMirror code) {
  background: #f5f5f5;
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

:deep(.ProseMirror pre) {
  background: #282c34;
  color: #abb2bf;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  margin: 0.5rem 0;
}

:deep(.ProseMirror pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

:deep(.ProseMirror blockquote) {
  border-left: 3px solid #42b983;
  padding-left: 1rem;
  margin: 0.5rem 0;
  font-style: italic;
}

:deep(.ProseMirror img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 0.5rem 0;
}

:deep(.ProseMirror table) {
  border-collapse: collapse;
  width: 100%;
  margin: 0.5rem 0;
}

:deep(.ProseMirror table td),
:deep(.ProseMirror table th) {
  border: 1px solid #ddd;
  padding: 0.5rem;
  text-align: left;
}

:deep(.ProseMirror table th) {
  background: #f5f5f5;
  font-weight: bold;
}

:deep(.ProseMirror a) {
  color: #42b983;
  text-decoration: underline;
}
</style>
