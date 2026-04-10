---
name: teavm-react-skill-updater
description: "Use this skill whenever someone asks to update, refresh, regenerate, or sync the teavm-react skill, or after significant changes to the library that the skill should reflect. Triggers on: update the skill, refresh the skill, sync the skill, regenerate API signatures, library changed update skill."
---

# teavm-react Skill Updater

This meta-skill keeps `skills/teavm-react/` in sync with the library as it evolves. It reads the library source, diffs against the last sync point, and proposes targeted updates to the skill files.

**Important:** This skill only reads library source and writes to `skills/teavm-react/`. It never modifies the library itself.

## Workflow

### Step 1: Read State

Load `state/last-updated.json` from this skill's directory. It tracks:
```json
{
  "last_synced_commit": "<commit hash>",
  "last_synced_version": "<version from pom.xml>",
  "last_synced_date": "<ISO timestamp>",
  "skill_path": "skills/teavm-react"
}
```

If the file doesn't exist, treat this as a first-time sync and examine the full current state of the library.

### Step 2: Diff the Library

Run these commands to understand what changed since the last sync:

```bash
# Commit log since last sync
git log --oneline <last_synced_commit>..HEAD -- \
  teavm-react-core/src/main/java/ca/weblite/teavmreact/ \
  teavm-react-kotlin/src/main/kotlin/ca/weblite/teavmreact/kotlin/ \
  teavm-react-demo/src/main/java/ \
  teavm-react-demo/src/main/webapp/ \
  docs/ \
  pom.xml \
  teavm-react-core/pom.xml \
  teavm-react-kotlin/pom.xml \
  teavm-react-demo/pom.xml \
  dev.sh run.sh DevServer.java \
  .github/workflows/

# Actual diff
git diff <last_synced_commit>..HEAD -- <same paths>
```

If the diff is huge (>100 commits or a major refactor), recommend the user review manually first and run this skill on smaller chunks.

### Step 3: Categorize Changes

Sort each change into one of these categories and determine which skill files need updating:

| Category | Skill files to update |
|----------|----------------------|
| **Breaking API change** (renamed/removed/signature-changed public method/class) | SKILL.md gotchas, `references/api-signatures.md`, affected per-approach reference file |
| **New public API** (new hook, HTML element, event handler, Kotlin DSL function) | `references/api-signatures.md`, relevant per-approach reference file |
| **New approach or DSL** | New `references/<approach>.md`, `references/approach-selection.md` |
| **TeaVM version bump** | `references/gotchas.md`, `references/teavm-interop.md`, `references/pom-templates.md` |
| **React version bump** (e.g., 18 → 19) | SKILL.md, `references/build-and-deploy.md`, `assets/examples/shared-html-shell/`, all example index.html files |
| **Build/tooling change** (dev.sh, run.sh, Maven profiles) | `references/build-and-deploy.md` |
| **HTML shell change** | `assets/examples/shared-html-shell/`, example index.html files |
| **New gotcha or troubleshooting item** | `references/gotchas.md`, possibly SKILL.md inline gotchas |
| **Documentation-only change** | Relevant reference files |
| **Internal/impl change** | Usually no update — note and skip |

### Step 4: Regenerate API Signatures

Run the generator script if it exists:

```bash
bash skills/teavm-react/scripts/generate-api-signatures.sh
```

This walks the source tree and emits `references/api-signatures.md`. If the script doesn't exist or fails, manually update signatures based on the diff.

Review the generated output for completeness — the script uses grep and may miss some patterns. Ensure both Java (`ca.weblite.teavmreact.*`) and Kotlin (`ca.weblite.teavmreact.kotlin.*`) packages are covered.

### Step 5: Propose Changes

Before editing any skill files, summarize findings to the user:

1. List each change category found
2. For each proposed edit, show: the target file, the rationale, and a brief preview of the change
3. Wait for user confirmation before applying

For large updates, batch by category and confirm each batch separately.

### Step 6: Apply Edits

After confirmation, edit the skill files. Follow these rules:

- **Preserve structure** — don't rewrite from scratch unless explicitly asked
- **Don't cross-contaminate approaches** — a Kotlin DSL change should not leak into `java-functional.md`
- **Keep SKILL.md under 500 lines** — route details to reference files
- **Update examples** if they reference changed APIs — verify imports, method names, and signatures
- **Be precise** — use exact class names, method names, and parameter types from the source

### Step 7: Validate

After edits, run these checks:

1. Count SKILL.md lines: `wc -l skills/teavm-react/SKILL.md` — must be under 500
2. Verify all referenced files exist:
   ```bash
   grep -oP 'references/\S+\.md' skills/teavm-react/SKILL.md | sort -u | while read f; do
     [ -f "skills/teavm-react/$f" ] || echo "MISSING: $f"
   done
   ```
3. Verify api-signatures.md covers both Java and Kotlin packages:
   ```bash
   grep -c 'ca.weblite.teavmreact.core' skills/teavm-react/references/api-signatures.md
   grep -c 'ca.weblite.teavmreact.kotlin' skills/teavm-react/references/api-signatures.md
   ```
4. Check that the HTML shell in `assets/examples/shared-html-shell/index.html` matches the demo's HTML:
   ```bash
   diff <(grep 'unpkg.com/react' teavm-react-demo/src/main/webapp/index.html) \
        <(grep 'unpkg.com/react' skills/teavm-react/assets/examples/shared-html-shell/index.html)
   ```
5. If possible, try `mvn process-classes` on example projects to verify they compile

### Step 8: Update State

Once the user confirms the update is complete, write the new state:

```bash
COMMIT=$(git rev-parse HEAD)
VERSION=$(grep '<version>' pom.xml | head -1 | sed 's/.*<version>//;s/<.*//')
DATE=$(date -u +%Y-%m-%dT%H:%M:%SZ)
```

Write to `.claude/skills/teavm-react-skill-updater/state/last-updated.json`:
```json
{
  "last_synced_commit": "<new commit>",
  "last_synced_version": "<current version>",
  "last_synced_date": "<current timestamp>",
  "skill_path": "skills/teavm-react"
}
```

Suggest committing the state file alongside the skill changes. Recommended commit message format:
```
Update teavm-react skill to <commit-short>

Synced skill with library changes: <brief summary of changes>
```

### Step 9: Suggest Next Steps

- If the version in `pom.xml` changed, suggest noting it in CHANGELOG or tagging a skill release
- If React or TeaVM version bumped, flag loudly — affects every user
- If major API changes occurred, suggest re-running the 5 self-test prompts from the skill creation plan

## Monitored Paths

These are the source paths that, when changed, may require a skill update:

```
teavm-react-core/src/main/java/ca/weblite/teavmreact/**    # Java API
teavm-react-kotlin/src/main/kotlin/ca/weblite/teavmreact/kotlin/**  # Kotlin DSL
teavm-react-demo/src/main/java/**                          # Demo patterns
teavm-react-demo/src/main/webapp/**                         # HTML shell
docs/**                                                      # Developer guide
pom.xml, */pom.xml                                           # Dependencies, versions
dev.sh, run.sh, DevServer.java                              # Build tooling
.github/workflows/**                                         # CI changes
```

## Constraints

- Never edit library source — only read it and write to `skills/teavm-react/`
- Never silently overwrite hand-written content — always propose and ask
- Pay special attention to the demo app (`teavm-react-demo/...App.java`) — it's the kitchen-sink reference. If it changes significantly, example projects may need parallel updates
- The photostream app (`webliteca/photostream`) is referenced as a real-world example. If its teavm-react usage patterns change significantly, update references accordingly
